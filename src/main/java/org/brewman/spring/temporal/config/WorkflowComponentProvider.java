package org.brewman.spring.temporal.config;

import io.temporal.workflow.WorkflowInterface;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class WorkflowComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public WorkflowComponentProvider() {
        super(false);
        super.addIncludeFilter(new AnnotationTypeFilter(WorkflowInterface.class, true, true));
    }
}
