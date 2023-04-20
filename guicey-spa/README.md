# Single page applications support

### About

Provides a replacement for dropwizard-assets bundle for single page applications (SPA) to properly
handle html5 client routing.

Features:

* Pure dropwizard bundle, but can be used with guicey bundles 
* Build above dropwizard-assets servlet
* Support registration on main and admin contexts
* Multiple apps could be registered
* Sets no-cache headers for index page
* Regex could be used to tune routes detection

#### Problem

The problem with SPA is html5 routing. For example, suppose your app base url is `/app`
and client route url is `/app/someroute` (before there were no problem because route would
look like `/app/#!/someroute`). When user hit refresh (or bookmark) such route, server is actually
called with route url. Server must recognize it and return index page.

For example, Angular 2 router use html5 mode my default.

#### Solution

The problem consists of two points:

1. Correctly process resource calls (css, js, images, etc) and return 404 for missed resources
2. Recognize application routes and return index page instead

Bundles register dropwizard-assets servlet with special filter above it. Filter tries to process
all incoming urls. This approach grants that all calls to resources will be processed and 
index page will not be returned instead of resource (solves problem 1).

If resource is not found - index page returned. To avoid redirection in case of bad resources request,
filter will redirect only requests accepting 'text/html'. Additional regexp (configurable) 
is used to recognize most resource calls and avoid redirection (show correct 404).

From example above, `/app/someroute` will return index page and `/app/css/some.css` will return css.
`/app/css/unknown.css` will return 404 as resource call will be recognized and css file is not exists.

### Setup

Maven:

```xml
<dependency>
  <groupId>ru.vyarus.guicey</groupId>
  <artifactId>guicey-spa</artifactId>
  <version>{guicey.version}</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus.guicey:guicey-spa:{guicey.version}'
```

Omit version if guicey BOM used.

### Usage

Register bundle:

```java
GuiceBundle.builder()
    .bundles(SpaBundle.app("app", "/app", "/").build());
```

This will register app with name "app" (name is used to name servlets and filters and must be unique).
Application files are located in "app" package in classpath (e.g. resources inside jar).
Application is mapped to root context (note that this will work only if rest is mapped 
to some sub context: e.g. with `server.rootPath: '/rest/*'` configuration).

```
http://localhost:8080/ -> app index
http://loclahost:8080/css/app.css -> application resource, located at /app/css/app.css in classpath
http://localhost:8080/someroute -> application client route - index page returned
```

Example registration to admin context:

```java
.bundles(SpaBundle.adminApp("admin", "com.mycompany.adminapp", "/manager").build());
```

Register "admin" application with resources in "/com/mycompany/adminapp/" package, served from "manager' 
admin context (note that admin root is already used by dropwizard admin servlet).

NOTE: resources location can be declared both as path (`/com/mycompany/adminapp/`) or as package (`com.mycompany.adminapp`).

```
http://localhost:8081/manager -> admin app index
```

You can register as many apps as you like. They just must use different urls and have different names:

```java
.bundles(SpaBundle.app("app", "/app", "/").build(),
         SpaBundle.app("app2", "/app2", "/").build(),
         SpaBundle.adminApp("admin", "/com/mycompany/adminapp/", "/manager").build(),
         SpaBundle.adminApp("admin2", "/com/mycompany/adminapp2/", "/manager2").build());
```

#### Index page

By default, index page assumed to be "index.html". Could be changed with:

```java
.bundles(SpaBundle.app("app", "/app", "/").indexPage("main.html").build());
```

#### Prevent redirect regex

By default, the following regex is used to prevent resources redirection (to not send index for missed resource):

```regexp
\.(html|css|js|png|jpg|jpeg|gif|ico|xml|rss|txt|eot|svg|ttf|woff|woff2|cur)(\?((r|v|rel|rev)=[\-\.\w]*)?)?$
```

Could be changed with:

```java
.bundles(SpaBundle.app("app", "/app", "/")
        .preventRedirectRegex("\\.\\w{2,5}(\\?.*)?$")
        .build());
```

This regexp implements naive assumption that all app routes does not contain "extension".

Note: regexp is applied with `find` so use `^` or `$` to apply boundaries. 
