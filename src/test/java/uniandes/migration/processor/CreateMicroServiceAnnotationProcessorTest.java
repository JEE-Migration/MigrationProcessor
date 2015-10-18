package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Microservice;
import generated.Migration;
import generated.Relation;
import generated.Relation.From;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import uniandes.migration.invoker.Invoker;

public class CreateMicroServiceAnnotationProcessorTest {

	private static final String PROCESSOR = "uniandes.migration.processor.CreateMicroServiceAnnotationProcessor";
	private static String LEGACY_CODE_PATH = "./src/legacy";
	
	
	@Test
	public void testFindMicroservices() {
		// Invoke spoon processor for methods
        
		Invoker invoker = new Invoker();
		invoker.invokeSpoon(PROCESSOR, LEGACY_CODE_PATH);
		
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
	}
	
	@Test
	public void testFindAtributes() {
		// Invoke spoon processor for methods
        
		Invoker invoker = new Invoker();
		invoker.invokeSpoon(PROCESSOR, LEGACY_CODE_PATH);
		
		Map<String, CtFieldReference<?>> atributesRequiringConsumeAnotation = CreateMicroServiceAnnotationProcessor
        		.getAtributesRequiringConsumeAnotation();
        assertTrue(atributesRequiringConsumeAnotation.size() == 1);
        assertTrue(atributesRequiringConsumeAnotation.containsKey("co.uniandes.app.MyClassA#a"));
	}
	
	@Test
	public void testFindMethods() {
		// Invoke spoon processor for methods
        
		Invoker invoker = new Invoker();
		invoker.invokeSpoon(PROCESSOR, LEGACY_CODE_PATH);
		
		Map<String, CtMethod<?>> atributesRequiringConsumeAnotation = CreateMicroServiceAnnotationProcessor
        		.getMethodsRequiringConsumeAnotation();
        assertTrue(atributesRequiringConsumeAnotation.size() == 1);
        assertTrue(atributesRequiringConsumeAnotation.containsKey("void foo(int)"));
	}
	
	
	
	@Test
	public void testReadPropertiesAtributes() {
		Migration migrationProp;
        migrationProp = CreateMicroServiceAnnotationProcessor.readProperties(
        		CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
        
        Map<String, Set<String>> fromTypeToRelatedAtributeTypes;
        fromTypeToRelatedAtributeTypes = CreateMicroServiceAnnotationProcessor
        		.getMapFromTypeToRelatedAtributeTypes(migrationProp);
        assertTrue(fromTypeToRelatedAtributeTypes.size() == 1);
        
        assertTrue(fromTypeToRelatedAtributeTypes.containsKey("co.uniandes.app.MyClassA"));
        assertTrue(fromTypeToRelatedAtributeTypes.get("co.uniandes.app.MyClassA").
        		contains("co.uniandes.app.MyClassA"));
        

	}
	
	@Test
	public void testReadPropertiesMethods() {
		Migration migrationProp;
        migrationProp = CreateMicroServiceAnnotationProcessor.readProperties(
        		CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
        
        Map<String, Map<String, Map<String, String>>> fromTypeToRelatedMethods;
        fromTypeToRelatedMethods = CreateMicroServiceAnnotationProcessor
        		.getMapFromTypeToRelatedMethod(migrationProp);
        assertTrue(fromTypeToRelatedMethods.size() == 1);
        
        assertTrue(fromTypeToRelatedMethods.containsKey("co.uniandes.app.MyClassB"));
        assertTrue(fromTypeToRelatedMethods.get("co.uniandes.app.MyClassB").
        		containsKey("void foo(int)"));
        
        assertTrue(fromTypeToRelatedMethods.get("co.uniandes.app.MyClassB").
        		get("void foo(int)").get("toQualifiedType").equals("co.uniandes.app.MyClassC"));
        assertTrue(fromTypeToRelatedMethods.get("co.uniandes.app.MyClassB").
        		get("void foo(int)").get("toMicroservice").equals("C"));
	
	}
	
	@Test
	public void testReadProperties() {
		Migration migration = CreateMicroServiceAnnotationProcessor.readProperties(
				CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
		assertTrue(migration != null);	
		
		Map<String, CtAnnotationType<?>> annotations;
		Map<String, Microservice> microservices;
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
