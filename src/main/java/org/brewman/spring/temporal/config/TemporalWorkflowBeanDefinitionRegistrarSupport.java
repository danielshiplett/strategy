package org.brewman.spring.temporal.config;

import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.workflow.TicketWorkflowImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class TemporalWorkflowBeanDefinitionRegistrarSupport
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(@Nonnull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(@Nonnull Environment environment) {
        this.environment = environment;
    }

    @Override
    @Deprecated
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata metadata,
                                        @Nonnull BeanDefinitionRegistry registry) {
        registerBeanDefinitions(metadata, registry, ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.beans.factory.support.BeanNameGenerator)
     */
    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata metadata,
                                        @Nonnull BeanDefinitionRegistry registry,
                                        @Nonnull BeanNameGenerator generator) {
        log.info("registerBeanDefinitions");

        Assert.notNull(metadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        // Guard against calls for sub-classes
        if (metadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        AnnotationWorkflowConfigurationSource configurationSource = new AnnotationWorkflowConfigurationSource(metadata,
                getAnnotation(), resourceLoader, environment, registry, generator);

        log.info("configurationSource: {}", configurationSource);

        registerWorkflowsIn(registry, configurationSource);
    }

    private void registerWorkflowsIn(BeanDefinitionRegistry registry, AnnotationWorkflowConfigurationSource configurationSource) {
        log.info("registerWorkflowsIn");

        configurationSource.getBasePackages();

        if (log.isInfoEnabled()) {
            log.info("Scanning for workflows in packages {}.",
                    configurationSource.getBasePackages().collect(Collectors.joining(", ")));
        }

        Stream<BeanDefinition> candidates = getCandidates(configurationSource);

        for (BeanDefinition candidate : candidates.collect(Collectors.toList())) {
            if(log.isInfoEnabled()) {
                log.info("Found candidate workflow {}", candidate.getBeanClassName());
            }

            Class<?> workflowClazz = loadWorkflowClass(candidate);
            log.info("Loading class {}", workflowClazz.getName());

            // TODO: The options need to come from properties.
            BeanDefinition workerBeanDefinition = createWorkerBeanDefinition(workflowClazz, TicketWorkflowImpl.TASK);

            // TODO: Need to generate the bean name.
            registry.registerBeanDefinition("SOMEWORKER", workerBeanDefinition);
            //registry.registerBeanDefinition(candidate.getBeanClassName(), candidate);
        }
    }

    private BeanDefinition createWorkerBeanDefinition(Class<?> workflowImplementationClass, String workerTaskQueue) {
        WorkerBeanDefinition beanDefinition = new WorkerBeanDefinition();
        beanDefinition.setBeanClassName(Worker.class.getName());
        beanDefinition.setFactoryBeanName("workerBeanFactory");
        beanDefinition.setFactoryMethodName("createWorker");

        /*
         * Unclear in the Spring docs, but the ConstructorArgumentValues will be passed as parameters to the
         * specified factory method.
         */
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(workflowImplementationClass);
        constructorArgumentValues.addGenericArgumentValue(workerTaskQueue);

        beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

        return beanDefinition;
    }

    private Class<?> loadWorkflowClass(BeanDefinition beanDefinition) {
        try {
            return ClassUtils.forName(beanDefinition.getBeanClassName(), resourceLoader.getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            log.warn("Could not load type {} with class loader {}", beanDefinition.getBeanClassName(), resourceLoader.getClassLoader());
        }

        return null;
    }

    private Stream<BeanDefinition> getCandidates(AnnotationWorkflowConfigurationSource configurationSource) {
        WorkflowComponentProvider workflowComponentProvider = new WorkflowComponentProvider();
        workflowComponentProvider.setEnvironment(environment);
        workflowComponentProvider.setResourceLoader(resourceLoader);

        return configurationSource.getBasePackages().flatMap(it -> workflowComponentProvider.findCandidateComponents(it).stream());
    }

    protected abstract Class<? extends Annotation> getAnnotation();
}
