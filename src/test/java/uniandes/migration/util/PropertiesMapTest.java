package uniandes.migration.util;

import static org.junit.Assert.*;
import uniandes.migration.generated.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import spoon.reflect.declaration.CtType;
import uniandes.migration.invoker.Invoker;
import uniandes.migration.processor.CreateMicroServiceAnnotationProcessor;
import uniandes.migration.util.PropertiesMap;

public class PropertiesMapTest {

	private static PropertiesMap propertiesMap;
	
	@BeforeClass
	public static void init(){
		propertiesMap = new PropertiesMap(CreateMicroServiceAnnotationProcessor.PATH_TO_MODEL_XML);
	}
	
	@Test
	public void testReadMicroservices(){
		Map<String, Microservice> microservices;
		
		microservices = propertiesMap.microservices;
        
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
        
        //Test related methods
        
        assertTrue(propertiesMap.serchingForTypeForMicroservice("co.uniandes.app.MyTypeA"));
        assertTrue(propertiesMap.serchingForTypeForMicroservice("co.uniandes.app.MyTypeB"));
        assertTrue(propertiesMap.serchingForTypeForMicroservice("co.uniandes.app.MyTypeC"));
        
        assertTrue(propertiesMap.getTypesMicroserviceName("co.uniandes.app.MyTypeA").equals("A"));
        assertTrue(propertiesMap.getTypesMicroserviceName("co.uniandes.app.MyTypeB").equals("B"));
        assertTrue(propertiesMap.getTypesMicroserviceName("co.uniandes.app.MyTypeC").equals("C"));
	}
	
	
	@Test
	public void testReadPropertiesAtributes() {
		
        assertTrue(propertiesMap.fromTypeToRelatedAtributeTypes.size() == 1);
        
        assertTrue(propertiesMap.fromTypeToRelatedAtributeTypes.containsKey("co.uniandes.app.MyClassA"));
        assertTrue(propertiesMap.fromTypeToRelatedAtributeTypes.get("co.uniandes.app.MyClassA").
        		containsKey("co.uniandes.app.MyClassA"));
        
        //Test related methods
        assertTrue(propertiesMap.serchingForTypeForAtribute("co.uniandes.app.MyClassA") == true);
        assertTrue(propertiesMap.serchingForFieldForAtribute("co.uniandes.app.MyClassA", "co.uniandes.app.MyClassA") == true);
        
        
	}
	
	@Test
	public void testReadPropertiesMethods() {
		
        assertTrue(propertiesMap.fromTypeToRelatedMethods.size() == 1);
        
        assertTrue(propertiesMap.fromTypeToRelatedMethods.containsKey("co.uniandes.app.MyClassB"));
        assertTrue(propertiesMap.fromTypeToRelatedMethods.get("co.uniandes.app.MyClassB").
        		containsKey("void foo1(int)"));
        
        //Test related methods
        assertTrue(propertiesMap.serchingForTypeForMethod("co.uniandes.app.MyClassB"));
        assertTrue(propertiesMap.serchingForMethodForMethod("co.uniandes.app.MyClassB", "void foo1(int)"));
        assertTrue(propertiesMap.getMethodsMicroserviceName("co.uniandes.app.MyClassB", "void foo1(int)").equals("C"));   
	}
	
	@Test
	public void testReadPropertiesMethodParameters() {
		
        assertTrue(propertiesMap.fromTypeToRelatedMethodParameters.size() == 1);
        
        assertTrue(propertiesMap.fromTypeToRelatedMethodParameters.containsKey("co.uniandes.app.MyClassC"));
        assertTrue(propertiesMap.fromTypeToRelatedMethodParameters.get("co.uniandes.app.MyClassC").
        		containsKey("void foo2(int)"));
        
        //Test related methods
       
        
	}

}
