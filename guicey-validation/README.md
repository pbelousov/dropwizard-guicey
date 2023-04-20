# Validation

### About

By default, dropwizard allows you to use validation annotations on [rest services](https://www.dropwizard.io/en/stable/manual/validation.html).
This allows you to use validation annotations the same way on any guice bean method.

Bundle is actually a wrapper for [guice-validator](https://github.com/xvik/guice-validator) project.

### Setup

Maven:

```xml
<dependency>
  <groupId>ru.vyarus.guicey</groupId>
  <artifactId>guicey-validation</artifactId>
  <version>{guicey.version}</version>
</dependency>
```

Gradle:

```groovy
implementation 'ru.vyarus.guicey:guicey-validation:{guicey.version}'
```

Omit version if guicey BOM used.

### Usage

By default, no setup required: bundle will be loaded automatically with the bundles lookup mechanism (enabled by default).
So just add jar into classpath and annotations will work.

For example:

```java
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import ru.vyarus.guicey.annotations.lifecycle.PostStartup;

public class SampleBean {    

    private void doSomething(@NotNull String param) {        
    }
    
}
```         

Call `bean.doSomething(null)` will fail with `ConstraintValidationException`.

For more usage examples see [guice-validator documentation](https://github.com/xvik/guice-validator#examples) 

#### Explicit mode

By default, validations work in implicit mode: any method containing validation annotations would trigger validation
on call.

If you want more explicitly mark methods requiring validation then register bundle manually:

```java
.bundles(new ValidationBundle()
                    .validateAnnotatedOnly())
```                                                     

Now, only methods annotated with `@ValidateOnExecution` (or all methods in annotated class)
will trigger validation.

If you want, you can use your own annotation:

```java
.bundles(new ValidationBundle()
                .validateAnnotatedOnly(MyAnnotation.class))
```                                                     

#### Reducing scope

By default, validation is not applied to resource classes (annotated with `@Path`) because
dropwizard already performs validation there. And rest methods, annotated with `@GET`, `@POST`, etc. 
are skipped (required for complex declaration cases, like dynamic resource mappings or sub resources). 

You can reduce this scope even further:

```java
.bundles(new ValidationBundle()
                    .targetClasses(Matchers.subclassesOf(SomeService.class)
                         .and(Matchers.not(Matchers.annotatedWith(Path.class)))))
```                                                     

Here `SomeService` is excluded from validation (its methods would not trigger validation). 
Note that default condition (not resource) is appended.


Or excluding methods:

```java
.bundles(new ValidationBundle()
                    .targetMethods(Matchers.annotatedWith(SuppressValidation.class)
                         .and(new DirectMethodMatcher())))
```

Now methods annotated with `@SuppressValidation` will not be validated. Note that
`.and(new DirectMethodMatcher())` condition was added to aslo exclude synthetic and bridge methods (jvm generated methods).

NOTE: you can verify AOP appliance with guicey `.printGuiceAopMap()` report.

#### Validation groups

By default, `Default` validation group is always enabled allowing you to not specify
groups for each call.

This could be disabled with bundle option:

```java
.bundles(new ValidationBundle().strictGroupsDeclaration())
```

Read more in [guice-validator docs](https://github.com/xvik/guice-validator#default-group-specifics).