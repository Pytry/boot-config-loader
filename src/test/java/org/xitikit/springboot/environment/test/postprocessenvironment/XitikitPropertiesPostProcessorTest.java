package org.xitikit.springboot.environment.test.postprocessenvironment;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.xitikit.springboot.environment.XitikitPropertiesPostProcessor;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Copyright Xitikit.org ${year}
 *
 * @author hoke1314
 */
public class XitikitPropertiesPostProcessorTest{

    @Test
    public void postProcessEnvironment(){

        ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
        SpringApplication application = mock(SpringApplication.class);
        MutablePropertySources sources = new MutablePropertySources();

        doReturn(sources)
            .when(environment)
            .getPropertySources();
        doReturn(Whatever.class)
            .when(application)
            .getMainApplicationClass();

        XitikitPropertiesPostProcessor propertiesProcessor = new XitikitPropertiesPostProcessor();
        propertiesProcessor.postProcessEnvironment(environment, application);

        assertTrue(sources.size() == 1);
        assertNotNull(sources.get("application"));
    }
}