package org.brewman.spring.temporal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR;

@Slf4j
public abstract class TemporalBeanDefinitionRegistrarSupport
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    @SuppressWarnings("null")
    @Nonnull
    private ResourceLoader resourceLoader;

    @SuppressWarnings("null")
    @Nonnull
    private Environment environment;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    @Deprecated
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerBeanDefinitions(metadata, registry, ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.beans.factory.support.BeanNameGenerator)
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator generator) {
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

/*        RepositoryConfigurationExtension extension = getExtension();
        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader,
                environment);

        delegate.registerRepositoriesIn(registry, extension);*/

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

           // registry.registerBeanDefinition(candidate.getBeanClassName(), candidate);
        }
    }

    private Stream<BeanDefinition> getCandidates(AnnotationWorkflowConfigurationSource configurationSource) {
        WorkflowComponentProvider workflowComponentProvider = new WorkflowComponentProvider();
        workflowComponentProvider.setEnvironment(environment);
        workflowComponentProvider.setResourceLoader(resourceLoader);

        return configurationSource.getBasePackages().flatMap(it -> workflowComponentProvider.findCandidateComponents(it).stream());
    }

    protected abstract Class<? extends Annotation> getAnnotation();
}
