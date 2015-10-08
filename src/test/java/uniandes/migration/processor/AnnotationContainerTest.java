package uniandes.migration.processor;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import spoon.reflect.declaration.CtAnnotationType;

public class AnnotationContainerTest {

	private static String TEST_ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/annotation";
	
	@Test
	public void test() {
		AnnotationContainer.readAnnotations(TEST_ANNOTATIONS_PATH);
		
		Map<String, CtAnnotationType<?>> annotations = AnnotationContainer.getAnnotations();
        assertTrue(annotations.size() == 4);
        assertTrue(annotations.get("uniandes.migration.annotation.Microservice") != null);
        assertTrue(annotations.get("uniandes.migration.annotation.Consumes") != null);
        assertTrue(annotations.get("uniandes.migration.annotation.Consume") != null);
        assertTrue(annotations.get("uniandes.migration.annotation.Provides") != null);
		
		
	}

}
