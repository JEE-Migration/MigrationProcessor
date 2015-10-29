package uniandes.migration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import uniandes.migration.enumeration.HttpMethod;
import uniandes.migration.microservices.*;
import uniandes.migration.microservices.Signature.Parameters.Parameter;

public class PropertiesMap implements IPropertiesMap{

	public final String pathToXmlModel;
	public final Migration migrationProp;
	
	public final Map<String, Microservice> microservices;
	public final Map<MethodKey, Set<MethodKey>> mapMethodToMethod;
	
	public final Map<String, Set<MethodKey>> mapFromTypeToFromMethod;
	public final Map<MethodKey, List<MethodInvocation>> mapFromMethodToData;
	
	public final Map<String, Set<MethodKey>> mapToTypeToToMethod;
	public final Map<MethodKey, List<MethodInvocation>> mapToMethodToData;
	
	
	public PropertiesMap(String pathToXmlModel){
		this.pathToXmlModel = pathToXmlModel;
		migrationProp = readProperties(this.pathToXmlModel);
		microservices = getMapFromTypeNameToMicorservice(migrationProp);
		mapMethodToMethod = getMapMethodToMethod(migrationProp);
		
		
		mapFromMethodToData = getMapFromMethodToData(migrationProp);
		mapFromTypeToFromMethod = getMapFromTypeToMethod(mapFromMethodToData.keySet());
		
		mapToMethodToData = getMapToMethodToData(migrationProp);
		mapToTypeToToMethod = getMapFromTypeToMethod(mapToMethodToData.keySet());
	}
	
	

	



	//-------
	public boolean searchingForMicroserviceType(String type) {
		return microservices.containsKey(type);
	}
	
	public String getTypesMicroservice(String type) {
		return (microservices.containsKey(type))? microservices.get(type).getName(): null;
	}

	public boolean searchingForTypeForMIFromMethod(String type) {
		return mapFromTypeToFromMethod.containsKey(type);
	}

	public boolean searchingForMIFromMethod(String type, String methodSignature) {
		return mapMethodToMethod.containsKey(new MethodKey(type, methodSignature));
	}

	public boolean searchingForTypeForMIToMethod(String type) {
		return mapToTypeToToMethod.containsKey(type);
	}

	public boolean searchingForMIToMethod(String type, String methodSignature) {
		return mapToMethodToData.containsKey(new MethodKey(type, methodSignature));
	}

	public Set<MethodKey> getToMethods(MethodKey fromMethod){
		return (mapMethodToMethod.containsKey(fromMethod))?mapMethodToMethod.get(fromMethod):null;
	}

	public String getMicroservice(String type, String methodSignature) {
		// TODO Auto-generated method stub
		return null;
	}

//	public MethodData getMethodProvidesData(String type, String methodSignature) {
//		String microservice = (microservices.containsKey(type))?microservices.get(type).getName(): null;
//		if(microservice == null){
//			return null;
//		}
//		
//		HttpMethod httpMethod  = calculateHttpMethod(methodSignature);
//		String [] parameterTypes = calculateParameterTypes();
//		Class returnType = null;
//		
//		return new MethodData(microservice, httpMethod, parameterTypes, returnType);
//	}
	
	
	

	
	//-----------------------------------------
    //HELPERS
    //-----------------------------------------

	public static Migration readProperties(String path) {
		return (Migration)JaxbWriterReader.jaxbReader(Migration.class, path);
	}

	public static Map<String, Microservice> getMapFromTypeNameToMicorservice(Migration migrationProp){
		Map<String, Microservice> microservices = new HashMap<String, Microservice>();
		for(Microservice m: migrationProp.getMicroservices().getMicroservice()){
			for(String type: m.getQualifiedType()){
				microservices.put(type, m);
			}
		}
		return microservices;
	}

	
	public static Map<MethodKey, Set<MethodKey>> getMapMethodToMethod(Migration migrationProp)
	{
		Map<MethodKey, Set<MethodKey>> methodMap = new HashMap<MethodKey, Set<MethodKey>>();
		for(Object o: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(o instanceof MethodInvocation){
				MethodInvocation method = (MethodInvocation)o;
				
				String fromType = method.getFrom().getQualifiedType();
				String fromMethodSignature = getMethodSignature(method.getFrom().getFromMethod());
				
				MethodKey fromMethod = new MethodKey(fromType, fromMethodSignature);
				
				String toType = method.getTo().getQualifiedType();
				String toMethodSignature = getMethodSignature(method.getTo().getToMethod());
				
				MethodKey toMethod = new MethodKey(toType, toMethodSignature);
				
				if(!methodMap.containsKey(fromMethod)){
					Set<MethodKey> toMethods = new HashSet<MethodKey>();
					methodMap.put(fromMethod, toMethods);
				}
				methodMap.get(fromMethod).add(toMethod);
			}
		}
		return methodMap;
	}
	
	public static Map<MethodKey, List<MethodInvocation>> getMapFromMethodToData(Migration migrationProp){
		Map<MethodKey, List<MethodInvocation>> methodMap = new HashMap<MethodKey, List<MethodInvocation>>();
		for(Object o: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(o instanceof MethodInvocation){
				MethodInvocation method = (MethodInvocation)o;
				
				String fromType = method.getFrom().getQualifiedType();
				String fromMethodSignature = getMethodSignature(method.getFrom().getFromMethod());
				
				MethodKey fromMethod = new MethodKey(fromType, fromMethodSignature);
				
				if(!methodMap.containsKey(fromMethod)){
					List<MethodInvocation> toSet = new ArrayList<MethodInvocation>();
					methodMap.put(fromMethod, toSet);
				}
				methodMap.get(fromMethod).add(method);
			}
		}
		return methodMap;
	}
	
	public static Map<String, Set<MethodKey>> getMapFromTypeToMethod(
			Set<MethodKey> methods)
	{
		Map<String, Set<MethodKey>> map = new HashMap<String, Set<MethodKey>>();
		for(MethodKey m: methods){
			if(!map.containsKey(m.getType())){
				Set<MethodKey> set = new HashSet<MethodKey>();
				map.put(m.getType(), set);
			}
			map.get(m.getType()).add(m);
		}
		return map;
	}
	
	
	
	private Map<MethodKey, List<MethodInvocation>> getMapToMethodToData(
			Migration migrationProp2) 
	{
		Map<MethodKey, List<MethodInvocation>> methodMap = new HashMap<MethodKey, List<MethodInvocation>>();
		for(Object o: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(o instanceof MethodInvocation){
				MethodInvocation method = (MethodInvocation)o;
				
				String toType = method.getTo().getQualifiedType();
				String toMethodSignature = getMethodSignature(method.getTo().getToMethod());
				
				MethodKey toMethod = new MethodKey(toType, toMethodSignature);
				
				if(!methodMap.containsKey(toMethod)){
					List<MethodInvocation> toSet = new ArrayList<MethodInvocation>();
					methodMap.put(toMethod, toSet);
				}
				methodMap.get(toMethod).add(method);
			}
		}
		return methodMap;
	}
	
	
	public static String getMethodSignature(Signature signature){
		StringBuilder sb = new StringBuilder();
		for(Parameter p: signature.getParameters().getParameter()){
			sb.append(p.getParamType());
			sb.append(",");
		}
		String params = (sb.length() > 0)? sb.substring(0, sb.length()-1): sb.toString();
		String methodSignature = signature.getReturnType() +  " " + signature.getMethodName() + "(" + params +")";
		return methodSignature;
	}


	


	
	







	
	
}
