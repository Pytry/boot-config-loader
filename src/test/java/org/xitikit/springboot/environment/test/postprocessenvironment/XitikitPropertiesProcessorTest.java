package org.xitikit.springboot.environment.test.postprocessenvironment;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.xitikit.springboot.environment.SpringConfigFile;
import org.xitikit.springboot.environment.XitikitPropertiesProcessor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
public class XitikitPropertiesProcessorTest{

    @SpringConfigFile("src\\test\\resources\\application.properties")
    private class Whatever{

    }

    @Test
    public void defaultConstructor(){

        new XitikitPropertiesProcessor();
        new XitikitPropertiesProcessor((String) null);
        new XitikitPropertiesProcessor((Package) null);
        new XitikitPropertiesProcessor("");
        new XitikitPropertiesProcessor(this.getClass().getPackage().toString());
        new XitikitPropertiesProcessor(this.getClass().getPackage());
    }


    @Test
    public void postProcessEnvironment(){

        ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
        SpringApplication application = mock(SpringApplication.class);
        MutablePropertySources sources = mock(MutablePropertySources.class);
        when(environment.getPropertySources()).thenReturn(sources);

        XitikitPropertiesProcessor propertiesProcessor = new XitikitPropertiesProcessor(this.getClass().getPackage());
        propertiesProcessor.postProcessEnvironment(environment, application);

        //Make sure that "addLast is called at least one time with a non-null value
        verify(sources, atLeastOnce()).addLast(notNull(PropertySource.class));
        //Make sure that "addLast" is never passed a null argument.
        verify(sources, never()).addLast(isNull(PropertySource.class));
    }
}