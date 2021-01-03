package org.brewman.temporal.autoconfigure;

public interface WorkflowFactory {
    <T> T makeWorkflowStub(Class<T> workflowInterface, String workflowName);
}
