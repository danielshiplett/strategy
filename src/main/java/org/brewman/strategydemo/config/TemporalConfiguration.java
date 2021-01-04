package org.brewman.strategydemo.config;

import lombok.extern.slf4j.Slf4j;
import org.brewman.temporal.annotations.EnableTemporal;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTemporal(
        workflowBasePackages = "org.brewman.strategydemo.temporal.workflows",
        activityBasePackages = "org.brewman.strategydemo.temporal.activities")
@Slf4j
public class TemporalConfiguration  {
    // No need to do anything else if you don't want to override the defaults or the values coming from the properties.
}
