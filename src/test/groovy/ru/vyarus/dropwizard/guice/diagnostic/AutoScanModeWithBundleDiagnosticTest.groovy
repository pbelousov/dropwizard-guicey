package ru.vyarus.dropwizard.guice.diagnostic

import io.dropwizard.Application
import ru.vyarus.dropwizard.guice.diagnostic.support.AutoScanAppWithBundle
import ru.vyarus.dropwizard.guice.diagnostic.support.bundle.*
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooInstaller
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooModule
import ru.vyarus.dropwizard.guice.diagnostic.support.features.FooResource
import ru.vyarus.dropwizard.guice.module.GuiceBootstrapModule
import ru.vyarus.dropwizard.guice.module.GuiceyConfigurationInfo
import ru.vyarus.dropwizard.guice.module.context.ConfigScope
import ru.vyarus.dropwizard.guice.module.context.info.InstallerItemInfo
import ru.vyarus.dropwizard.guice.module.context.info.ItemId
import ru.vyarus.dropwizard.guice.module.installer.CoreInstallersBundle
import ru.vyarus.dropwizard.guice.module.installer.WebInstallersBundle
import ru.vyarus.dropwizard.guice.module.installer.feature.LifeCycleInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.TaskInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.eager.EagerSingletonInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyFeatureInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider.JerseyProviderInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.plugin.PluginInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.WebFilterInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.WebServletInstaller
import ru.vyarus.dropwizard.guice.module.installer.feature.web.listener.WebListenerInstaller
import ru.vyarus.dropwizard.guice.module.installer.scanner.ClasspathScanner
import ru.vyarus.dropwizard.guice.test.jupiter.TestGuiceyApp

import javax.inject.Inject

import static ru.vyarus.dropwizard.guice.module.context.info.ItemId.typesOnly

/**
 * @author Vyacheslav Rusakov
 * @since 26.06.2016
 */
@TestGuiceyApp(AutoScanAppWithBundle)
class AutoScanModeWithBundleDiagnosticTest extends BaseDiagnosticTest {

    @Inject
    GuiceyConfigurationInfo info

    def "Check diagnostic info correctness"() {

        expect: "correct bundles info"
        info.guiceyBundles as Set == [FooBundle, FooBundleRelativeBundle, CoreInstallersBundle, WebInstallersBundle] as Set
        info.bundlesFromLookup.isEmpty()

        and: "correct installers info"
        def classes = [FooInstaller, FooBundleInstaller,
                       JerseyFeatureInstaller,
                       JerseyProviderInstaller,
                       ResourceInstaller,
                       EagerSingletonInstaller,
                       HealthCheckInstaller,
                       TaskInstaller,
                       PluginInstaller,
                       WebFilterInstaller,
                       WebServletInstaller,
                       WebListenerInstaller]
        info.installers as Set == classes as Set
        info.installersDisabled as Set == [LifeCycleInstaller, ManagedInstaller] as Set
        info.installersFromScan == [FooInstaller]

        and: "correct extensions info"
        info.extensions as Set == [FooResource, FooBundleResource] as Set
        info.extensionsFromScan == [FooResource]
        info.getExtensions(ResourceInstaller) as Set == [FooResource, FooBundleResource] as Set
        info.getExtensions(FooBundleInstaller).isEmpty()

        and: "correct modules"
        info.modules as Set == [FooModule, GuiceBootstrapModule, FooBundleModule] as Set

        and: "correct scopes"
        typesOnly(info.getActiveScopes()) as Set == [Application, ClasspathScanner, CoreInstallersBundle, FooBundle, WebInstallersBundle] as Set
        typesOnly(info.getItemsByScope(ConfigScope.Application)) as Set == [CoreInstallersBundle, FooBundle, FooModule, GuiceBootstrapModule] as Set
        typesOnly(info.getItemsByScope(ConfigScope.ClasspathScan)) as Set == [FooInstaller, FooResource] as Set
        typesOnly(info.getItemsByScope(FooBundle)) as Set == [FooBundleInstaller, FooBundleResource, FooBundleModule, FooBundleRelativeBundle] as Set

        and: "lifecycle installer was disabled"
        !info.getItemsByScope(CoreInstallersBundle).contains(LifeCycleInstaller)
        InstallerItemInfo li = info.getInfo(LifeCycleInstaller)
        !li.enabled
        li.disabledBy == [ItemId.from(Application)] as Set
        li.registered

        and: "managed installer was disabled"
        !info.getItemsByScope(CoreInstallersBundle).contains(ManagedInstaller)
        InstallerItemInfo mi = info.getInfo(ManagedInstaller)
        !mi.enabled
        mi.disabledBy == [ItemId.from(FooBundle)] as Set
        mi.registered
    }
}
