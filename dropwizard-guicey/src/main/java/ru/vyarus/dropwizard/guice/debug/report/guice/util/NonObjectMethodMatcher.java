package ru.vyarus.dropwizard.guice.debug.report.guice.util;

import com.google.inject.matcher.AbstractMatcher;

import java.lang.reflect.Method;

/**
 * Matcher accept all methods, except methods declared in {@link Object}.
 *
 * @author Vyacheslav Rusakov
 * @since 23.08.2019
 */
public class NonObjectMethodMatcher extends AbstractMatcher<Method> {

    @Override
    public boolean matches(final Method o) {
        return !Object.class.equals(o.getDeclaringClass());
    }
}
