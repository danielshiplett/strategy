package org.brewman.temporal.autoconfigure;

import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.temporal.workflows.TicketWorkflow;
import org.brewman.temporal.annotations.BaseNameAnnotationConfigurationSource;
import org.brewman.temporal.annotations.EnableTemporal;
import org.brewman.temporal.annotations.TemporalAnnotationConfigurationSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TemporalRegistrar extends TemporalBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getEnableAnnotation() {
        return EnableTemporal.class;
    }

    @Override
    protected BaseNameAnnotationConfigurationSource getConfigurationSource(
            AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        return new TemporalAnnotationConfigurationSource(metadata,
                getEnableAnnotation(), resourceLoader, environment, registry, generator);
    }

    @Override
    protected void registerBeansIn(BeanDefinitionRegistry registry, BaseNameAnnotationConfigurationSource configurationSource) {

        Assert.isInstanceOf(
                TemporalAnnotationConfigurationSource.class,
                configurationSource, "ConfigurationSource must be an AnnotationWorkflowConfigurationSource");

        TemporalAnnotationConfigurationSource temporalAnnotationConfigurationSource = (TemporalAnnotationConfigurationSource)configurationSource;

        registerActivitiesIn(registry, temporalAnnotationConfigurationSource);
        registerWorkflowsIn(registry, temporalAnnotationConfigurationSource);
    }

    private void registerActivitiesIn(BeanDefinitionRegistry registry, BaseNameAnnotationConfigurationSource configurationSource) {
        log.info("registerActivitiesIn");

        if (log.isInfoEnabled()) {
            log.info("Scanning for activities in packages {}.",
                    configurationSource.getActivityPackages().collect(Collectors.joining(", ")));
        }

        Stream<BeanDefinition> candidates = getCandidates(
                configurationSource.getActivityPackages(),
                new TemporalActivityComponentProvider());

        for (BeanDefinition candidate : candidates.collect(Collectors.toList())) {
            if (log.isInfoEnabled()) {
                log.info("Found candidate activity {}", candidate.getBeanClassName());
            }

            Class<?> activityClazz = loadClassFromBeanDefinition(candidate);
            log.info("Found class {}", activityClazz.getName());

            BeanDefinition beanDefinition = createActivityBeanDefinition(activityClazz);
            registry.registerBeanDefinition("SOMEACTIVITY", beanDefinition);
        }
    }

    private void registerWorkflowsIn(BeanDefinitionRegistry registry, TemporalAnnotationConfigurationSource configurationSource) {
        log.info("registerWorkflowsIn");

        if (log.isInfoEnabled()) {
            log.info("Scanning for workflows in packages {}.",
                    configurationSource.getWorkflowBasePackages().collect(Collectors.joining(", ")));
        }

        Stream<BeanDefinition> candidates = getCandidates(
                configurationSource.getWorkflowBasePackages(),
                new TemporalWorkflowComponentProvider());

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

    private BeanDefinition createActivityBeanDefinition(Class<?> activityImplementationClass) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(activityImplementationClass.getName());
        return beanDefinition;
    }
}
