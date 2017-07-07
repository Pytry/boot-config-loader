package org.xitikit.springboot.environment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
public class XitikitPropertiesPostProcessor implements EnvironmentPostProcessor{

    private static final Logger log = Logger.getLogger(XitikitPropertiesPostProcessor.class.getName());
    private final SpelExpressionParser SpEL = new SpelExpressionParser();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application){

        BootConfigPath annotation = findAnnotation(application);
        if(annotation == null){
            log.warning(BootConfigPath.class.getSimpleName() + " was not found on the main application class. Exiting without loading configuration.");
            return;
        }
        addPropertySourceLast(annotation.path(), environment.getPropertySources());
    }

    private BootConfigPath findAnnotation(SpringApplication application){

        Class<?> mainApplicationClass = application.getMainApplicationClass();
        return mainApplicationClass == null ? null : mainApplicationClass.getAnnotation(BootConfigPath.class);
    }

    private void addPropertySourceLast(String path, MutablePropertySources propertySources){

        path = trimToNull(path);
        if(path == null){
            throw new IllegalArgumentException(BootConfigPath.class.getSimpleName() + " was found on the main application class, but the value/path was missing.");
        }
        path = aparecium(path);
        if(hasValidExtension(path)){
            propertySources.addLast(buildPropertySource(path));
        }
        else{
            throw new IllegalArgumentException(BootConfigPath.class.getSimpleName() + ".path() resolved to an invalid file format.");
        }
    }

    /**
     * Reveals invisible messages hidden by SpEL.
     *
     * @param secret That which contains the charm
     * @return The hidden charm.
     */
    private String aparecium(String secret){

        assert secret != null;

        if(isFilePath(secret)){
            // No secrets here:(
            return secret.replace("\\", "/");
        }
        Expression expression = SpEL.parseExpression(secret);
        String result = trimToEmpty(expression != null ? (String) expression.getValue() : "").replace("\\", "/");
        if(!isFilePath(result)){

            throw new IllegalArgumentException("The value for path could not be resolved to a valid file path. Resolved value was '" + result + "'.");
        }
        return result;
    }

    private boolean isFilePath(String path){

        assert path != null;

        try{
            Paths.get(isOnClasspath(path) ? trimClasspathPrefix(path) : trimFilePrefix(path));
            return true;
        }
        catch(InvalidPathException ignore){
            return false;
        }
    }

    private boolean hasValidExtension(String value){

        assert value != null;

        return value.endsWith(".yml")
            || value.endsWith(".json")
            || value.endsWith(".properties");
    }

    private PropertySource<?> buildPropertySource(final String path){

        assert isNotBlank(path);

        final Resource resource = buildResource(path);

        if(!resource.exists()){
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try{
            return buildLoader(path).load(parseResourceName(path), resource, null);
        }
        catch(IOException ex){
            throw new IllegalStateException("Failed to load configuration from " + path, ex);
        }
    }

    private String parseResourceName(String path){

        assert path != null : "In 'private String parseResourceName(String path)' arguments cannot be null.";
        assert !path.contains("\\") : "In 'private String parseResourceName(String path)' path should not contain left slashes ('\\').";

        return path.substring(
            path.lastIndexOf("/") + 1,
            path.lastIndexOf("."));
    }

    private Resource buildResource(String path){

        assert path != null;

        return isOnClasspath(path) ? buildClasspathResource(path) : buildFileSystemResource(path);
    }

    private Resource buildClasspathResource(String path){

        return new ClassPathResource(trimClasspathPrefix(path));
    }

    private Resource buildFileSystemResource(String path){

        return new FileSystemResource(trimFilePrefix(path));
    }

    private String trimClasspathPrefix(String path){

        assert isOnClasspath(path);
        return trimToEmpty(path.substring(10)); //'10' accounts for the colon as well.
    }

    private String trimFilePrefix(String path){

        assert path != null;
        assert !isOnClasspath(path);

        return trimToEmpty(path.startsWith("file:") ? path.substring(5) : path); //'5' accounts for the colon as well
    }

    private PropertySourceLoader buildLoader(final String path){

        final PropertySourceLoader loader;

        if(path.endsWith(".yml") || path.endsWith(".json")){
            loader = new YamlPropertySourceLoader();
        }
        else if(path.endsWith(".properties")){
            loader = new PropertiesPropertySourceLoader();
        }
        else{
            throw new IllegalArgumentException("Invalid file format found for '" + path + "'. Extension matching is case sensitive, and must be either '.yml', '.json', or '.properties'.");
        }
        return loader;
    }

    private boolean isOnClasspath(String path){

        return path.startsWith("classpath:");
    }

//    @SuppressWarnings("unused")
//    private String replaceLeftSlash(final String value){
//
//        StringBuilder answer = new StringBuilder(value);
//
//        if(value.contains("#") || value.contains("$")){
//
//            for(int i = 0; i < value.length(); i++){
//                replaceIfNotEscapeChar(i,answer);
//            }
//        }
//
//        return answer.toString();
//    }
//    private boolean replaceIfNotEscapeChar(final int index, final StringBuilder value){
//
//        final char slashMaybe = value.charAt(index);
//        if(slashMaybe == '\\'){
//            if(!isSpecialChar(index + 1, value)){
//
//
//            }
//        }
//        return false;
//    }

//    private boolean isSpecialChar(final int index, final StringBuilder value){
//
//        boolean yes = true;
//
//        char val = value.charAt(index);
//        switch(val){
//            // +, -, *, /, %, ^, div, mod
//            case '#': break;
//            case '{': break;
//            case '}': break;
//            case '$': break;
//            case '+': break;
//            case '-': break;
//            case '*': break;
//            case '/': break;
//            case '%': break;
//            case '^': break;
//            case 'd':
//                yes = value.charAt(index + 1) != 'i' || value.charAt(index + 2) != 'v';
//                break;
//            case 'm':
//                yes = value.charAt(index + 1) == 'o' && value.charAt(index + 2) == 'd';
//                break;
//            // <, >, ==, !=, <=, >=, lt, gt, eq, ne, le, ge
//            case '<': break;
//            case '>': break;
//            case '=': break;
//            case '!': break;
//            case 'g':
//                yes = value.charAt(index + 1) == 't' || value.charAt(index + 1) == 'e';
//                break;
//            case 'l':
//                yes = value.charAt(index + 1) == 't' || value.charAt(index + 1) == 'e';
//                break;
//            case 'e':
//                yes = value.charAt(index + 1) == 'q';
//                break;
//            case 'n': break;
//            case '': break;
//            case '': break;
//            default: yes = false;
//        }
//        return yes;
//    }
}
