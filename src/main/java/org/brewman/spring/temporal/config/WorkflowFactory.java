package org.brewman.spring.temporal.config;

public interface WorkflowFactory {
    <T> T makeWorkflowStub(Class<T> workflowInterface, String workflowName);
}
