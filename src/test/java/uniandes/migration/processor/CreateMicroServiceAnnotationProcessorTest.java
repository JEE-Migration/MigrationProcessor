package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Migration;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;

public class CreateMicroServiceAnnotationProcessorTest {

	private static final String PROCESSOR = "uniandes.migration.processor.CreateMicroServiceAnnotationProcessor";
	private static String LEGACY_CODE_PATH = "./src/legacy";
	
	
	@Test
	public void test() {
		// Invoke spoon processor for methods
        String[] spoonArgs = new String[6];
        spoonArgs[0] = "-i";
        spoonArgs[1] = LEGACY_CODE_PATH;
        spoonArgs[2] = "-p";
        spoonArgs[3] = PROCESSOR;
        spoonArgs[4] = "--compliance";
        spoonArgs[5] = "7";
        try {
            Launcher.main(spoonArgs);
        } catch (Exception e) {
            System.err.println("Error while executing spoon launcher "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            fail();
        }
	}
	
	@Test
	public void testReadProperties() {
		Migration migration = CreateMicroServiceAnnotationProcessor.readProperties(
				CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
		assertTrue(migration != null);	
	}

}
