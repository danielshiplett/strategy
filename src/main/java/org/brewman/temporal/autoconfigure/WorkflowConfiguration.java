package org.brewman.temporal.autoconfigure;

import io.temporal.client.WorkflowOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.temporal.activities.TicketActivitiesImpl;
import org.brewman.strategydemo.temporal.workflows.TicketWorkflowImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import static org.brewman.strategydemo.temporal.workflows.TicketWorkflow.TASK;

@RequiredArgsConstructor
@Slf4j
public class WorkflowConfiguration implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

    private final TemporalProperties temporalProperties;
    private final WorkerFactory workerFactory;

    private BeanFactory beanFactory;

    @Override
    @Nullable
    public Object postProcessAfterInitialization(@NonNull final Object bean, @NonNull final String beanName) {
        log.info("postProcessAfterInitialization: {} / {}", bean, beanName);

        if(!beanName.equalsIgnoreCase("TicketWorkflow")) {
            return bean;
        }

        log.info("postProcessAfterInitialization: {}", beanName);

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK)
                .build();

        Worker worker = workerFactory.newWorker(TASK);

        worker.registerActivitiesImplementations(new TicketActivitiesImpl());
        worker.registerWorkflowImplementationTypes(TicketWorkflowImpl.class);


        return bean;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (temporalProperties.isCreateWorkers()) {
            workerFactory.start();
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
