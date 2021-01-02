package org.brewman.spring.temporal.config;

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
import org.springframework.core.ResolvableType;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class WorkerBeanFactory extends AbstractAutowireCapableBeanFactory {

    private final WorkflowClient workflowClient;

    public Worker createWorker(Class<?> workflowImplementationClass, String workerTaskQueue) {
        log.warn("createWorker: {}, {}", workflowImplementationClass, workerTaskQueue);

        WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);

        Worker worker = workerFactory.newWorker(workerTaskQueue);
        worker.registerWorkflowImplementationTypes(workflowImplementationClass);
        workerFactory.start();

        return  worker;
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
