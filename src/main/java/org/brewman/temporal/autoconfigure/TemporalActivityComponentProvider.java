package org.brewman.temporal.autoconfigure;

import io.temporal.activity.ActivityInterface;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class TemporalActivityComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public TemporalActivityComponentProvider() {
        super(false);
        super.addIncludeFilter(new AnnotationTypeFilter(ActivityInterface.class, true, true));
    }
}
