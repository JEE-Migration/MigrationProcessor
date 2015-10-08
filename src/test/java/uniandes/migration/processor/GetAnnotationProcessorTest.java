package uniandes.migration.processor;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;

public class GetAnnotationProcessorTest {

	private static final String PROCESSOR = "uniandes.migration.processor.GetAnnotationProcessor";
	private static String TEST_ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/annotation";
	
//	private static String TEST_ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/processor";
	
	
	@Test
	public void test() {
		
		// Invoke spoon processor for methods
        String[] spoonArgs = new String[6];
        spoonArgs[0] = "-i";
        spoonArgs[1] = TEST_ANNOTATIONS_PATH;
        spoonArgs[2] = "-p";
        spoonArgs[3] = PROCESSOR;
        spoonArgs[4] = "--compliance";
        spoonArgs[5] = "7";
        try {
            Launcher.main(spoonArgs);
            Map<String, CtAnnotationType<?>> annotations = AnnotationContainer.getAnnotations();
            assertTrue(annotations.size() == 4);
            assertTrue(annotations.get("uniandes.migration.annotation.Microservice") != null);
            assertTrue(annotations.get("uniandes.migration.annotation.Consumes") != null);
            assertTrue(annotations.get("uniandes.migration.annotation.Consume") != null);
            assertTrue(annotations.get("uniandes.migration.annotation.Provides") != null);
        } catch (Exception e) {
            System.err.println("Error while executing spoon launcher "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            fail();
        }
        
	}

}
