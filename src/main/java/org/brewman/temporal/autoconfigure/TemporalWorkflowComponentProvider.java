package org.brewman.temporal.autoconfigure;

import io.temporal.workflow.WorkflowInterface;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class TemporalWorkflowComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public TemporalWorkflowComponentProvider() {
        super(false);
        super.addIncludeFilter(new AnnotationTypeFilter(WorkflowInterface.class, true, true));
    }
}
