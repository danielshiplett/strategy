package org.brewman.temporal.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TemporalActivityRegistrar extends TemporalBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getEnableAnnotation() {
        return EnableTemporalActivities.class;
    }

    @Override
    protected BaseNameAnnotationConfigurationSource getConfigurationSource(
            AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        return new AnnotationActivityConfigurationSource(metadata,
                getEnableAnnotation(), resourceLoader, environment, registry, generator);
    }

    @Override
    protected ClassPathScanningCandidateComponentProvider getComponentProvider() {
        return new TemporalActivityComponentProvider();
    }

    @Override
    protected void registerBeansIn(BeanDefinitionRegistry registry, BaseNameAnnotationConfigurationSource configurationSource) {

        Assert.isInstanceOf(
                AnnotationActivityConfigurationSource.class,
                configurationSource, "ConfigurationSource must be an AnnotationActivityConfigurationSource");

        AnnotationActivityConfigurationSource annotationActivityConfigurationSource = (AnnotationActivityConfigurationSource)configurationSource;

        registerActivitiesIn(registry, annotationActivityConfigurationSource);
    }

    private void registerActivitiesIn(BeanDefinitionRegistry registry, AnnotationActivityConfigurationSource configurationSource) {
        log.info("registerActivitiesIn");

        configurationSource.getBasePackages();

        if (log.isInfoEnabled()) {
            log.info("Scanning for activities in packages {}.",
                    configurationSource.getBasePackages().collect(Collectors.joining(", ")));
        }

        Stream<BeanDefinition> candidates = getCandidates(configurationSource);

        for (BeanDefinition candidate : candidates.collect(Collectors.toList())) {
            if (log.isInfoEnabled()) {
                log.info("Found candidate activity {}", candidate.getBeanClassName());
            }

            Class<?> activityClazz = loadClassFromBeanDefinition(candidate);
            log.info("Found class {}", activityClazz.getName());
        }
    }
}
