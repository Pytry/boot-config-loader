package org.xitikit.springboot.environment;

import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.lang.annotation.*;

/**
 * Used to define the path(s) of application properties used in Spring Boots
 * auto configuration process. This annotation will get processed before the
 * application context is loaded.
 * When using this annotation, do not use "${your.var}" to inject values, as this may result
 * in the expression not being processed. Instead, use SpEL in the following manner to use
 * custom JVM parameters to indicate the path(s).
 * Example:
 * \@BootConfigPath("#{systemProperties['your.jvm.parameter']}")
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BootConfigPath{

    /**
     * Alias for {@link #path}.
     * Intended to be used when no other attributes are needed.
     *
     * @see #path
     */
    @AliasFor("path")
    String value() default "";

    /**
     * Alias for {@link #value}. The path may also be configured via the {@link #value}
     * attribute if no other attributes are declared.
     * This should hold either a fully qualified or relative path to a properties file
     * ending in ".yml", ".json", or ".properties".
     * The path cannot resolve to a directory.
     * Standard properties files are loaded using {@link PropertiesPropertySourceLoader}.
     * Both YAML and JSON files are loaded using {@link YamlPropertySourceLoader}
     * By default, this will look for the resource in the filesystem. Giving the path a
     * prefix of "file:" is allowed for clarity, but it is not required since this
     * is the default behaviour. {@link FileSystemResource} is used for accessing the
     * resource by default.
     * If you wish for properties to load a value from the classpath, then the value must
     * have a prefix of "classpath:". You should never use this ability to load properties
     * that could just as easily be included in the standard classpath of filesystem locations
     * for standard Spring Boot configuration files. It is really only intended for loading
     * standard Third-Party properties. {@link ClassPathResource} is used for accessing
     * the resource.
     * Whitespace between the prefix and the resource path is allowed, but will be ignored
     * when resolving the path.     *
     * Spring Expression Language can be used to inject the value of custom system property
     * (ie, JVM parameter)
     * Null or empty values can be used, but will cause the loader to do nothing (such as
     * those null/empty values that may result from the evaluation of a SpEL script).
     * Example 1:
     * {@code @BootConfigPath("../external-properties/my-app.yml")}
     * This will load a file called "my-app.yml" located in a directory
     * called "external-properties" that is parallel to the running directory
     * of the application.
     * Example 2:
     * {@code @BootConfigPath("#{systemProperties['my-app.config.path']}?:''")}
     * This will load the value of a system property called "my-app.config.path" and attempt to
     * use it to retrieve the resource. If the property is not present, then it will default to
     * an empty value and no properties will be loaded.
     *
     * @see #value
     */
    @AliasFor("value")
    String path() default "";
}
