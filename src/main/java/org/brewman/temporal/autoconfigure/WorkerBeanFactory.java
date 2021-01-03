package org.brewman.temporal.autoconfigure;

import io.temporal.activity.ActivityInterface;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import java.util.Map;
import java.util.Set;

/**
 * The WorkerBeanFactory will create a Worker for the given workflow implementation class.  Right now, this is a
 * one-to-one implementation.
 */
@RequiredArgsConstructor
@Slf4j
public class WorkerBeanFactory extends AbstractAutowireCapableBeanFactory {

    private final ApplicationContext applicationContext;
    private final WorkflowClient workflowClient;

    public Worker createWorker(Class<?> workflowImplementationClass, String workerTaskQueue) {
        log.warn("createWorker: {}, {}", workflowImplementationClass, workerTaskQueue);
        Map<String, Object> activityBeans = getActivityBeans();

        WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);

        Worker worker = workerFactory.newWorker(workerTaskQueue);
        worker.registerWorkflowImplementationTypes(workflowImplementationClass);

        /*
         * For now, just register all activity implmentations with every worker.
         */
        for(Map.Entry<String, Object> entry : activityBeans.entrySet()) {
            log.info("registering activity {} to worker", entry.getKey());
            worker.registerActivitiesImplementations(entry.getValue());
        }

        workerFactory.start();

        return  worker;
    }

    private Map<String, Object> getActivityBeans() {
        return applicationContext.getBeansWithAnnotation(ActivityInterface.class);
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        log.warn("resolveNamedBean");
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        log.warn("resolveDependency");
        return null;
    }

    @Override
    protected boolean containsBeanDefinition(String beanName) {
        log.warn("containsBeanDefinition");
        return false;
    }

    @Override
    protected BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        log.warn("getBeanDefinition");
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        log.warn("getBean");
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        log.warn("getBean");
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        log.warn("getBeanProvider");
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        log.warn("getBeanProvider");
        return null;
    }
}
