package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Microservice;
import generated.Migration;
import generated.Relation;
import generated.Relation.From;

import java.util.HashMap;
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
	public void testFindMicroservices() {
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
		
		Map<String, CtAnnotationType<?>> annotations;
		Map<String, Microservice> microservices;
		Map<String, Relation> fromAtribute;
		Map<String, Relation> fromMethodInvocation;
		Map<String, Relation> fromMethodParameter;
		
		Map<String, Relation> toAtribute;
		Map<String, Relation> toMethodInvocation;
		Map<String, Relation> toMethodParameter;
		
		Migration migrationProp;
		
        migrationProp = CreateMicroServiceAnnotationProcessor.readProperties(CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
        
        microservices = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToMicorservice(migrationProp);
        
        assertTrue(microservices.size() == 9);
        assertTrue(microservices.containsKey("co.uniandes.app.MyTypeA"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyTypeB"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyTypeC"));
        
        assertTrue(microservices.containsKey("co.uniandes.app.MyInterfaceA"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyInterfaceB"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyInterfaceC"));
        
        assertTrue(microservices.containsKey("co.uniandes.app.MyClassA"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyClassB"));
        assertTrue(microservices.containsKey("co.uniandes.app.MyClassC"));
        
        fromAtribute = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToFromAtribute(migrationProp);
        assertTrue(fromAtribute.size() == 1);
        
        assertTrue(fromAtribute.containsKey("co.uniandes.app.MyClassA"));
        
        fromMethodInvocation = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToFromMethodInvocation(migrationProp);
        assertTrue(fromMethodInvocation.size() == 1);
        
        assertTrue(fromMethodInvocation.containsKey("co.uniandes.app.MyClassB"));
        
        fromMethodParameter = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToFromMethodParameter(migrationProp);
        assertTrue(fromMethodParameter.size() == 1);
        
        assertTrue(fromMethodParameter.containsKey("co.uniandes.app.MyClassC"));
        
        
        
        toAtribute = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToToAtribute(migrationProp);
        assertTrue(toAtribute.size() == 1);
        
        assertTrue(toAtribute.containsKey("co.uniandes.app.MyClassA"));
        
        toMethodInvocation = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToToMethodInvocation(migrationProp);
        assertTrue(toMethodInvocation.size() == 1);
        
        assertTrue(toMethodInvocation.containsKey("co.uniandes.app.MyClassC"));
        
        toMethodParameter = CreateMicroServiceAnnotationProcessor
        		.getMapFromNameToToMethodParameter(migrationProp);
        assertTrue(toMethodParameter.size() == 1);
        
        assertTrue(toMethodParameter.containsKey("co.uniandes.app.MyClassA"));
        
	}

}
