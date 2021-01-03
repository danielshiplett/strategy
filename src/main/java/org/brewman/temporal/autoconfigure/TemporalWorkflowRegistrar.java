package org.brewman.temporal.autoconfigure;

import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.temporal.workflows.TicketWorkflow;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class TemporalWorkflowRegistrar extends TemporalBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getEnableAnnotation() {
        return EnableTemporalWorkflows.class;
    }

    @Override
    protected BaseNameAnnotationConfigurationSource getConfigurationSource(
            AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        return new AnnotationWorkflowConfigurationSource(metadata,
                getEnableAnnotation(), resourceLoader, environment, registry, generator);
    }

    @Override
    protected ClassPathScanningCandidateComponentProvider getComponentProvider() {
        return new TemporalWorkflowComponentProvider();
    }

    @Override
    protected void registerBeansIn(BeanDefinitionRegistry registry, BaseNameAnnotationConfigurationSource configurationSource) {

        Assert.isInstanceOf(
                AnnotationWorkflowConfigurationSource.class,
                configurationSource, "ConfigurationSource must be an AnnotationWorkflowConfigurationSource");

        AnnotationWorkflowConfigurationSource annotationWorkflowConfigurationSource = (AnnotationWorkflowConfigurationSource)configurationSource;

        registerWorkflowsIn(registry, annotationWorkflowConfigurationSource);
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

            Class<?> workflowClazz = loadClassFromBeanDefinition(candidate);
            log.info("Found class {}", workflowClazz.getName());

            // TODO: The options need to come from properties.
            String taskQueue = TicketWorkflow.TASK;
            BeanDefinition workerBeanDefinition = createWorkerBeanDefinition(workflowClazz, taskQueue);

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
}
