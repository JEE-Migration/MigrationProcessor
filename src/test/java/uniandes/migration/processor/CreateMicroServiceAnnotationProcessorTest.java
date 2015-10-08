package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Migration;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtType;

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
            Map<String, CtType<?>> requiresMicroserviceAnnotation = CreateMicroServiceAnnotationProcessor
            		.getRequiresMicroserviceAnnotation();
            assertTrue(requiresMicroserviceAnnotation.size() == 9);
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyClassA"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyClassB"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyClassC"));
            
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyInterfaceA"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyInterfaceB"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyInterfaceC"));
            
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyTypeA"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyTypeB"));
            assertTrue(requiresMicroserviceAnnotation.containsKey("co.uniandes.app.MyTypeC"));
            
        } catch (Exception e) {
            System.err.println("Error while executing spoon launcher "
                    + e.getMessage());
            e.printStackTrace();
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
