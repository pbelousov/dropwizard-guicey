package ru.vyarus.dropwizard.guice.module.installer.feature;

import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
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
 * Lifecycle objects installer.
 * Looks for classes implementing {@code org.eclipse.jetty.util.component.LifeCycle} and register them in environment.
 *
 * @author Vyacheslav Rusakov
 * @since 01.09.2014
 */
@Order(10)
public class LifeCycleInstaller implements FeatureInstaller, InstanceInstaller<LifeCycle>, Ordered {

    private final Reporter reporter = new Reporter(LifeCycleInstaller.class, "life cycles =");

    @Override
    public boolean matches(final Class<?> type) {
        return FeatureUtils.is(type, LifeCycle.class);
    }

    @Override
    public void install(final Environment environment, final LifeCycle instance) {
        reporter.line(RenderUtils.renderClassLine(FeatureUtils.getInstanceClass(instance)));
        environment.lifecycle().manage(instance);
    }

    @Override
    public void report() {
        reporter.report();
    }

    @Override
    public List<String> getRecognizableSigns() {
        return Collections.singletonList("implements " + LifeCycle.class.getSimpleName());
    }
}
