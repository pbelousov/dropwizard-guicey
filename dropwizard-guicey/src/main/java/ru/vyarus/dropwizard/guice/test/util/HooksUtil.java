package ru.vyarus.dropwizard.guice.test.util;

import ru.vyarus.dropwizard.guice.hook.GuiceyConfigurationHook;
import ru.vyarus.dropwizard.guice.module.installer.util.InstanceUtils;
import ru.vyarus.dropwizard.guice.test.EnableHook;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Guicey {@link ru.vyarus.dropwizard.guice.hook.GuiceyConfigurationHook} test utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 02.05.2020
 */
public final class HooksUtil {

    private HooksUtil() {
    }

    /**
     * Validate fields annotated with {@link EnableHook} for correctness.
     *
     * @param fields                fields to validate
     * @param includeInstanceFields true to allow instance fields, false to break if instance field detected
     */
    public static void validateFieldHooks(final List<Field> fields, final boolean includeInstanceFields) {
        for (Field field : fields) {
            if (!GuiceyConfigurationHook.class.isAssignableFrom(field.getType())) {
                throw new IllegalStateException(String.format(
                        "Field %s annotated with @%s, but its type is not %s",
                        toString(field), EnableHook.class.getSimpleName(), GuiceyConfigurationHook.class.getSimpleName()
                ));
            }
            if (!includeInstanceFields && !Modifier.isStatic(field.getModifiers())) {
                throw new IllegalStateException(String.format("Field %s annotated with @%s must be static",
                        toString(field), EnableHook.class.getSimpleName()));
            }
        }
    }

    /**
     * Instantiates provided hooks.
     *
     * @param hooks hooks to instantiate
     * @return hooks instances
     */
    @SafeVarargs
    public static List<GuiceyConfigurationHook> create(final Class<? extends GuiceyConfigurationHook>... hooks) {
        final List<GuiceyConfigurationHook> res = new ArrayList<>();
        for (Class<? extends GuiceyConfigurationHook> hook : hooks) {
            try {
                res.add(InstanceUtils.create(hook));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate guicey hook: " + hook.getSimpleName(), e);
            }
        }
        return res;
    }

    /**
     * Register configuration hooks.
     *
     * @param hooks hooks to register
     */
    public static void register(final List<GuiceyConfigurationHook> hooks) {
        if (hooks != null) {
            for (GuiceyConfigurationHook hook : hooks) {
                if (hook != null) {
                    hook.register();
                }
            }
        }
    }

    private static String toString(final Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }
}
