package ru.vyarus.dropwizard.guice.test.spock.ext;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.dropwizard.guice.hook.GuiceyConfigurationHook;
import ru.vyarus.dropwizard.guice.test.util.HooksUtil;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Base class for guicey spock extensions. Extensions use {@link DropwizardTestSupport} internally.
 *
 * @param <T> extension annotation
 * @author Vyacheslav Rusakov
 * @since 03.01.2015
 */
public abstract class AbstractAppExtension<T extends Annotation> extends AbstractAnnotationDrivenExtension<T> {

    private T annotation;

    @Override
    public void visitSpecAnnotation(final T useApplication, final SpecInfo spec) {
        this.annotation = useApplication;
    }

    @Override
    public void visitSpec(final SpecInfo spec) {
        final Class<?> testType = spec.getReflection();
        final List<GuiceyConfigurationHook> hooks = SpecialFieldsSupport.findHooks(testType);
        hooks.addAll(HooksUtil.create(getHooks(annotation)));
        final GuiceyInterceptor interceptor =
                new GuiceyInterceptor(spec, buildSupport(annotation, testType), hooks);
        final SpecInfo topSpec = spec.getTopSpec();
        topSpec.addSharedInitializerInterceptor(interceptor);
        topSpec.addInitializerInterceptor(interceptor);
        topSpec.addCleanupSpecInterceptor(interceptor);
    }

    /**
     * @param annotation extension annotation instance
     * @return configuration hooks defined in annotation
     */
    protected abstract Class<? extends GuiceyConfigurationHook>[] getHooks(T annotation);

    /**
     * @param annotation extension annotation instance
     * @param test       test class
     * @return environment support object
     */
    protected abstract GuiceyInterceptor.EnvironmentSupport buildSupport(T annotation, Class<?> test);

    /**
     * Utility method to convert configuration overrides from annotation to rule compatible format.
     *
     * @param overrides override annotations
     * @return dropwizard config override objects
     */
    protected ConfigOverride[] convertOverrides(
            final ru.vyarus.dropwizard.guice.test.spock.ConfigOverride... overrides) {
        final ConfigOverride[] configOverride = new ConfigOverride[overrides.length];
        int i = 0;
        for (ru.vyarus.dropwizard.guice.test.spock.ConfigOverride override : overrides) {
            configOverride[i++] = ConfigOverride.config(override.key(), override.value());
        }
        return configOverride;
    }
}
