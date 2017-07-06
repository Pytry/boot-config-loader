package org.xitikit.springboot.environment;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Used to define the location(s) of application properties used in Spring Boots
 * auto configuration process. This annotation will get processed before the
 * application context is loaded.
 * When using this annotation, do not use "${your.var}" to inject values, as this may result
 * in the expression not being processed. Instead, use SpEL in the following manner to use
 * custom JVM parameters to indicate the location(s).
 * Example:
 * \@SpringConfigFile("#{systemProperties['your.jvm.parameter']}")
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SpringConfigFile{

    /**
     * Alias for {@link #location}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @SpringConfigFile("my-app.config.location")}.
     *
     * @see #location
     */
    @AliasFor("location")
    String[] value() default "";

    /**
     * Alias for {@link #value}.
     *
     * This should hold the locations of application properties to be loaded by spring boot.
     *
     * If at least one value is not given, or the value is blank or all whitespace, then the value
     * is assumed to be the current directory that the JVM is running under.
     *
     * If a value resolves to a filesystem directory, then it must be the only
     * value given; multiple directories are not allowed as this may cause duplicate
     * files to be loaded.
     *
     * If a value resolves to a directory and no values are set for {@link #fileName},
     * then all properties files with valid extensions will be loaded from that directory.
     *
     * By default, this will look for the resource in the filesystem. If you
     * wish for it to load a value from the classpath, then the value must
     * have a prefix of "classpath:".
     *
     * Giving the value a prefix of "file:" is also allowed for clarity, but it is not required
     * since this is the default behaviour.
     *
     * Whitespace between the prefix and the resource location is allowed, but will be ignored
     * when resolving.
     *
     * File extensions are case-sensitive and must match exactly
     * to either ".yml",".json", or ".properties").
     *
     * The locations and may also be configured via the {@link #value}
     * attribute if no other attributes are declared.
     *
     * @see #value
     */
    @AliasFor("value")
    String[] location() default "";

    /**
     * If only one location is given and it is a directory,
     * then the fileName can be used to indicate which files
     * should be loaded from that directory.
     *
     * If this is left blank or no values are given, and a valuethen it is assumed that all files are loaded that have a valid
     * file extension (".yml",".json",".properties").
     *
     * The values for 'fileName' must not resolve to a directory.
     *
     * If {@link #location} is left unspecified/empty, then this value must not be null, blank, nor all whitespace.
     *
     * @return An array of file names to be loaded as properties.
     */
    String[] fileName() default {};
}
