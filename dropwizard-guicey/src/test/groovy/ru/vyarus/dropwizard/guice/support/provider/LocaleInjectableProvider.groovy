package ru.vyarus.dropwizard.guice.support.provider


import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.ext.Provider
import java.util.function.Supplier

/**
 * Sample injectable provider which injects Locale instance from request.
 *
 * @author Vyacheslav Rusakov 
 * @since 09.10.2014
 */
@Provider
class LocaleInjectableProvider implements Supplier<Locale> {
    static int creationCounter = 0
    static int callCounter = 0

    private javax.inject.Provider<HttpHeaders> request;

    @Inject
    LocaleInjectableProvider(javax.inject.Provider<HttpHeaders> request) {
        creationCounter++
        this.request = request
    }

    @Override
    Locale get() {
        callCounter++
        final List<Locale> locales = request.get().getAcceptableLanguages();
        return locales.isEmpty() ? Locale.US : locales.get(0);
    }

    public static void resetCounters() {
        creationCounter = 0
        callCounter = 0
    }
}
