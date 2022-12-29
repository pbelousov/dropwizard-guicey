package ru.vyarus.dropwizard.guice.test.util;

import com.google.common.base.Preconditions;
import io.dropwizard.testing.ConfigOverride;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.vyarus.dropwizard.guice.debug.util.RenderUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Config override handling utils.
 *
 * @author Vyacheslav Rusakov
 * @since 30.04.2020
 */
public final class ConfigOverrideUtils {

    private ConfigOverrideUtils() {
    }

    /**
     * Unique prefix is important because config overrides works through system properties and without unique prefix
     * it would be impossible to use parallel tests.
     * <p>
     * Because extension might be used per-method, prefix must follow current test hierarchy (counting nested tests
     * and executed test method).
     *
     * @param context test context
     * @return unique properties prefix to use for this test
     */
    public static String createPrefix(final ExtensionContext context) {
        // extension per-method support
        final Optional<Method> method = context.getTestMethod();
        String prefix = method.map(Method::getName)
                .orElseGet(() -> RenderUtils.getClassName(context.getRequiredTestClass()));
        // nested tests support
        if (context.getParent().isPresent() && context.getParent().get().getTestClass().isPresent()) {
            prefix = createPrefix(context.getParent().get()) + "." + prefix;
        }
        return prefix;
    }

    /**
     * @param prefix prefix
     * @param props  overriding properties in "key: value" format
     * @return parsed configuration override objects
     */
    public static ConfigOverride[] convert(final String prefix, final String... props) {
        ConfigOverride[] overrides = null;
        if (props != null && props.length > 0) {
            overrides = new ConfigOverride[props.length];
            int i = 0;
            for (String value : props) {
                final int idx = value.indexOf(':');
                Preconditions.checkState(idx > 0 && idx < value.length(),
                        "Incorrect configuration override declaration: must be 'key: value', but found '%s'", value);
                overrides[i++] = ConfigOverride
                        .config(prefix, value.substring(0, idx).trim(), value.substring(idx + 1).trim());
            }
        }
        return overrides;
    }

    /**
     * Merges config override arrays.
     *
     * @param base     existing overrides (may be null)
     * @param addition additional overrides (may be empty)
     * @return merged overrides
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public static String[] mergeRaw(final String[] base, final String... addition) {
        if (addition == null || addition.length == 0) {
            return base;
        }
        if (base == null || base.length == 0) {
            return addition;
        }
        final String[] res = new String[base.length + addition.length];
        System.arraycopy(base, 0, res, 0, base.length);
        System.arraycopy(addition, 0, res, base.length, addition.length);
        return res;
    }

    /**
     * Adds config override for existing overrides array.
     *
     * @param base     existing overrides (may be null)
     * @param addition additional overrides (may be empty)
     * @return merged overrides
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public static ConfigOverride[] merge(final ConfigOverride[] base, final ConfigOverride... addition) {
        if (addition == null || addition.length == 0) {
            return base;
        }
        if (base == null) {
            return addition;
        }
        final ConfigOverride[] res = new ConfigOverride[base.length + addition.length];
        System.arraycopy(base, 0, res, 0, base.length);
        System.arraycopy(addition, 0, res, base.length, addition.length);
        return res;
    }

    /**
     * Process provided custom config override objects by setting context prefix.
     *
     * @param prefix test specific prefix
     * @param values objects to process
     * @param <T>    composite helper type
     * @return array of processed objects or null if nothing registered
     */
    public static <T extends ConfigOverride & ConfigurablePrefix> ConfigOverride[] prepareOverrides(
            final String prefix, final List<T> values) {
        ConfigOverride[] res = null;
        if (!values.isEmpty()) {
            res = new ConfigOverride[values.size()];
            int i = 0;
            for (T value : values) {
                value.setPrefix(prefix);
                res[i++] = value;
            }
        }
        return res;
    }

    /**
     * Process config overrides set by junit extensions.
     *
     * @param overrides array of all configured config overrides
     * @param context   extension contest to resolve storage from
     * @return same array
     */
    public static ConfigOverride[] prepareExtensionOverrides(final ConfigOverride[] overrides,
                                                             final ExtensionContext context) {
        if (overrides != null) {
            for (ConfigOverride override : overrides) {
                if (override instanceof ConfigOverrideExtensionValue) {
                    ((ConfigOverrideExtensionValue) override).resolveValue(context);
                }
            }
        }
        return overrides;
    }
}
