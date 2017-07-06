package org.xitikit.springboot.environment.test.multiplenames;

import org.junit.Test;
import org.reflections.Reflections;
import org.xitikit.springboot.environment.SpringConfigFile;

import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Copyright Bear River Mutual ${year}
 *
 * @author hoke1314
 */
public class TestMultipleNames{

    @Test
    public void testScan(){

        Package aPackage = this.getClass().getPackage();
        Reflections reflections = new Reflections(aPackage);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(SpringConfigFile.class);
        assertNotNull("'getTypesAnnotatedWith' returned null for package " + aPackage, types);
        assertTrue("Expected one class in package " + aPackage + " to be annotated. Found " + types.size() + " classes with '@SpringConfigFile'.", types.size() == 1);

        types.forEach(
            t -> {
                SpringConfigFile annotation = t.getAnnotation(SpringConfigFile.class);
                String[] value = annotation.value();
                assertTrue("Expected two names. Found " + value.length + " values.", value.length == 1);
                assertNotNull("The first locations was null or empty.", trimToNull(value[0]));
                assertNotNull("The second locations was null or empty.", trimToNull(value[1]));
            }
        );
    }

    @SpringConfigFile(location = {"classpath:application.properties", "file:config/application.yml"})
    private class Whatever{

    }
}
