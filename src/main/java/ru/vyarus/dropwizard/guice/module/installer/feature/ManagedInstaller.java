package ru.vyarus.dropwizard.guice.module.installer.feature;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.core.setup.Environment;
import ru.vyarus.dropwizard.guice.debug.util.RenderUtils;
import ru.vyarus.dropwizard.guice.module.installer.FeatureInstaller;
import ru.vyarus.dropwizard.guice.module.installer.install.InstanceInstaller;
import ru.vyarus.dropwizard.guice.module.installer.order.Order;
import ru.vyarus.dropwizard.guice.module.installer.order.Ordered;
import ru.vyarus.dropwizard.guice.module.installer.util.FeatureUtils;
import ru.vyarus.dropwizard.guice.module.installer.util.Reporter;

import java.util.Collections;
import java.util.List;

/**
 * Managed objects installer.
 * Looks for classes implementing {@code io.dropwizard.lifecycle.Managed} and register them in environment.
 *
 * @author Vyacheslav Rusakov
 * @since 01.09.2014
 */
@Order(20)
public class ManagedInstaller implements FeatureInstaller, InstanceInstaller<Managed>, Ordered {

    private final Reporter reporter = new Reporter(ManagedInstaller.class, "managed =");

    @Override
    public boolean matches(final Class<?> type) {
        return FeatureUtils.is(type, Managed.class);
    }

    @Override
    public void install(final Environment environment, final Managed instance) {
        reporter.line(RenderUtils.renderClassLine(FeatureUtils.getInstanceClass(instance)));
        environment.lifecycle().manage(instance);
    }

    @Override
    public void report() {
        reporter.report();
    }

    @Override
    public List<String> getRecognizableSigns() {
        return Collections.singletonList("implements " + Managed.class.getSimpleName());
    }
}
