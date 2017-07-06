package org.xitikit.springboot.environment;

import org.reflections.Reflections;
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
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
public class XitikitPropertiesProcessor implements EnvironmentPostProcessor{

    private static final String howToUse = "" +
        "'location' cannot contain a mix of directories and property file paths." +
        "'fileName' must have at least one value if 'location' contains directories." +
        "'fileName' must not be given any values if 'location' contains property file paths (no directories)." +
        "'fileName' be a simple file name (no '/' or '\\') with a valid extension." +
        "";
    private final String packageToScan;

    private final SpelExpressionParser SpEL;

    public XitikitPropertiesProcessor(){

        this("");
    }

    public XitikitPropertiesProcessor(String packageToScan){

        this.packageToScan = trimToEmpty(packageToScan);
        SpEL = new SpelExpressionParser();
    }

    public XitikitPropertiesProcessor(Package packageToScan){

        this(packageToScan != null ? packageToScan.toString() : "");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application){

        addAllPropertySources(environment.getPropertySources());
    }

    private void addAllPropertySources(MutablePropertySources propertySources){

        springConfigPath().forEach(
            path -> {
                addPropertySourcesFromPath(evaluateSpEL(path), propertySources);
            }
        );
    }

    private String evaluateSpEL(String path){

        return (String) SpEL.parseExpression(trimToEmpty(path)).getValue();
    }

    private void addPropertySourcesFromPath(final String source, final MutablePropertySources propertySources){

        if(source != null){

            List<PropertySource<?>> propertySource = buildPropertySources(source);
            propertySource.forEach(
                propertySources::addLast
            );
        }
    }

    private PropertySource<?> buildPropertySources(final String path){

        boolean isClassPathResource = path.startsWith("classpath:");
        if(isClassPathResource){

        }
    }

    private PropertySource<?> buildPropertySource(final String path){

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

        assert trimToNull(path) != null : "In 'private String parseResourceName(String path)' arguments cannot be null nor empty strings.";

        final int slashIndex = path.lastIndexOf("/");
        return path.substring(
            (slashIndex <= 0 ? path.lastIndexOf("\\") : slashIndex) + 1,
            path.lastIndexOf("."));
    }

    private Resource buildResource(final String path){

        return path.startsWith("classpath:") ? buildClasspathResource(path) : buildFileSystemResource(path);
    }

    private Resource buildClasspathResource(String path){

        return new ClassPathResource(trimClasspathPrefix(path));
    }

    private Resource buildFileSystemResource(String path){

        return new FileSystemResource(trimFilePrefix(path));
    }

    private String trimPrefix(String path){

        assert path != null;
        return trimPrefix(path, path.startsWith("classpath"));
    }

    private String trimPrefix(String path, boolean isClasspathResourceName){

        assert path != null;
        return isClasspathResourceName ? trimClasspathPrefix(path) : trimFilePrefix(path)
    }

    private String trimClasspathPrefix(String path){

        return path.substring(10); //'10' accounts for the colon as well.
    }

    private String trimFilePrefix(String path){

        return path.startsWith("file:") ? path.substring(5) : path; //'5' accounts for the colon as well
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

    /**
     * @return the locations of the jvm parameter
     * that contains the location of the Spring Boot property file to be loaded.
     */
    private List<String> configurationPaths(){

    }

    /**
     * @return the locations of the jvm parameter
     * that contains the location of the Spring Boot property file to be loaded.
     */
    private List<String> springConfigPath(){

        return new Reflections(packageToScan)
            .getTypesAnnotatedWith(SpringConfigFile.class)
            .stream()
            .collect(
                LinkedList<String>::new,
                (a, b) -> buildPaths(a, b),
                LinkedList::addAll
            );
    }

    private List<String> buildPaths(List<String> paths, SpringConfigFile springConfigFile){

        assert paths != null;
        assert springConfigFile != null;

        final List<String>
            answer = new LinkedList<>(),
            locationList = buildNullSafeResourcePathList(springConfigFile.location()),
            fileNameList = buildNullSafeResourcePathList(springConfigFile.fileName());

        for(String location : locationList){
            addAllFiles(answer, location, fileNameList);
        }

        return answer;
    }

    private void addAllFiles(final List<String> answer, final String path, final List<String> fileNameList){

         if(isDirectory(path)){
            addAllFilesInDirectory(answer, path, fileNameList);
        }
        else if(hasValidExtension(path)){
            answer.add(path);
        }
    }

    private void addAllFilesInDirectory(final List<String> answer, final String path, final List<String> fileNameList){

        if(fileNameList.size() < 1 && isOnClasspath(path)){
            throw new IllegalArgumentException("Invalid classpath resources defined. If a location is a directory on the classpath, then at least one valid fileName must be defined.");
        }
        for(String name : fileNameList){
            if(hasValidExtension(name)){
                answer.add(
                    combine(path, name)
                );
            }
            else{
                throw new IllegalArgumentException("Invalid fileName value found. " +
                    "All fileName values must have a valid extension ('.yml','.json','.properties'). " +
                    "'location' values cannot have both directories and file paths, and cannot be null, empty, nor all whitespace." +
                    "");
            }
        }
    }

    private String combine(String directory, String fileName){

        return formatDirectoryPath(directory) + formatFileName(fileName);
    }

    private String formatDirectoryPath(String path){

        return path.endsWith("/") ? path : path + "/";
    }

    private String formatFileName(String name){

        return name.startsWith("/") ? name.substring(1, name.length());
    }

    private boolean hasValidExtension(String value){

        return value.endsWith(".yml")
            || value.endsWith(".json")
            || value.endsWith(".properties");
    }

    private boolean isDirectory(String value){

        return value.lastIndexOf(".") <= value.lastIndexOf("/");
    }

    private List<String> buildPathsForDirectory(Path path, List<String> fileNames){

        if(fileNames.size() == 1){

        }
    }

    private List<String> buildNullSafeResourcePathList(final String[] values){

        List<String> trimmed = new LinkedList<>();
        if(values != null){
            for(final String value : values){
                String val = trimToNull(value);
                if(val != null){
                    trimmed.add(val);
                }
            }
        }
        return trimmed;
    }

    private boolean isOnClasspath(String path){

        return path.startsWith("classpath:");
    }
}
