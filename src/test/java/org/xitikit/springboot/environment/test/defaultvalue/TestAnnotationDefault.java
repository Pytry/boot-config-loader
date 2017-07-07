package org.xitikit.springboot.environment.test.defaultvalue;

import org.junit.Test;
import org.reflections.Reflections;
import org.xitikit.springboot.environment.BootConfigPath;

import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Copyright Bear River Mutual ${year}
 *
 * @author hoke1314
 */
public class TestAnnotationDefault{

    @Test
    public void testScan(){

        Reflections reflections = new Reflections("org.xitikit.springboot.environment.test.defaultvalue");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(BootConfigPath.class);
        assertNotNull("'getTypesAnnotatedWith' returned null for package " + "org.xitikit.springboot.environment.test.defaultvalue", types);
        assertTrue("Expected one class in package " + "org.xitikit.springboot.environment.test.defaultvalue" + " to be annotated. Found " + types.size() + " classes with '@BootConfigPath'.", types.size() == 1);

        Class<?> t = types.iterator().next();
        assertNotNull("The class with the annotation was null.", t);
        BootConfigPath annotation = t.getAnnotation(BootConfigPath.class);
        assertNotNull("The class " + t.getSimpleName() + " did not contain the " + BootConfigPath.class.getSimpleName() + " annotation.", annotation);
        String value = trimToNull(annotation.value());
        assertNotNull("The default value was null or blank.", value);
    }
}
