package uniandes.migration.util;

import static org.junit.Assert.*;
import uniandes.migration.microservices.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import spoon.reflect.declaration.CtType;
import uniandes.migration.enumeration.HttpMethod;
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
	public void testSearchingForMicroserviceType(){
		
		assertFalse(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyClassD"));
		
        assertTrue(propertiesMap.microservices.size() == 9);
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyTypeA"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyTypeB"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyTypeC"));
        
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyInterfaceA"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyInterfaceB"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyInterfaceC"));
        
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyClassA"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyClassB"));
        assertTrue(propertiesMap.searchingForMicroserviceType("co.uniandes.app.MyClassC"));
        
        //Return
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyTypeA").equals("A"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyTypeB").equals("B"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyTypeC").equals("C"));
        
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyInterfaceA").equals("A"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyInterfaceB").equals("B"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyInterfaceC").equals("C"));
        
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyClassA").equals("A"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyClassB").equals("B"));
        assertTrue(propertiesMap.getTypesMicroservice("co.uniandes.app.MyClassC").equals("C"));
        
	}
	
	@Test
	public void testSearchingForTypeForMIFromMethod() {
		assertFalse(propertiesMap.searchingForTypeForMIFromMethod("co.uniandes.app.MyClassD"));
		assertTrue(propertiesMap.searchingForTypeForMIFromMethod("co.uniandes.app.MyClassB"));
	}
	
	
	@Test
	public void testSearchingForMIFromMethod() {
		
		assertFalse(propertiesMap.searchingForMIFromMethod("co.uniandes.app.MyClassD", "int foo(int)"));
		assertFalse(propertiesMap.searchingForMIFromMethod("co.uniandes.app.MyClassB", "void foo2(int)"));
		
		assertTrue(propertiesMap.mapFromMethodToData.size() == 1);
		assertTrue(propertiesMap.searchingForMIFromMethod("co.uniandes.app.MyClassB", "void foo1(int)"));
		 
	}

	
	@Test
	public void testSearchingForTypeForMIToMethod() {
		assertFalse(propertiesMap.searchingForTypeForMIToMethod("co.uniandes.app.MyClassB"));
		assertTrue(propertiesMap.searchingForTypeForMIToMethod("co.uniandes.app.MyClassC"));
	}
	
	@Test
	public void testSearchingForMIToMethod() {
		assertFalse(propertiesMap.searchingForMIToMethod("co.uniandes.app.MyClassD", "int foo(int)"));
		assertFalse(propertiesMap.searchingForMIToMethod("co.uniandes.app.MyClassC", "void bar(double)"));
		
		assertTrue(propertiesMap.mapFromMethodToData.size() == 1);
		assertTrue(propertiesMap.searchingForMIToMethod("co.uniandes.app.MyClassC", "void bar1(int)"));
	}
	
	
	

}
