package uniandes.migration.processor;

import static org.junit.Assert.*;
import generated.Attribute;
import generated.MethodInvocation;
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
import util.PropertiesMap;

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

}
