package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Migration;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;
import uniandes.migration.invoker.Invoker;

public class GetAnnotationProcessorTest {

	private static final String PROCESSOR = "uniandes.migration.processor.GetAnnotationProcessor";
	private static String TEST_ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/annotation";
	
	@Test
	public void test() {
		
		// Invoke spoon processor for methods
		Invoker invoker = new Invoker();
		invoker.invokeSpoon(PROCESSOR, TEST_ANNOTATIONS_PATH);
		
		Map<String, CtAnnotationType<?>> annotations = GetAnnotationProcessor.getAnnotations();
		assertTrue(annotations.size() == 4);
		assertTrue(annotations.get("uniandes.migration.annotation.Microservice") != null);
		assertTrue(annotations.get("uniandes.migration.annotation.Consumes") != null);
		assertTrue(annotations.get("uniandes.migration.annotation.Consume") != null);
		assertTrue(annotations.get("uniandes.migration.annotation.Provides") != null);

	}
	
	

}
