package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import generated.Attribute;
import generated.MethodInvocation;
import generated.MethodParameter;
import generated.Microservice;
import generated.Migration;
import generated.Relation;
import generated.Signature.Parameters.Parameter;

public class PropertiesMap {

	public final String pathToXmlModel;
	public final Migration migrationProp;
	
	//---------
	public final Map<String, Microservice> microservices;
	public final Map<String, Map<String, Attribute>> fromTypeToRelatedAtributeTypes;
	public final Map<String, Map<String, MethodInvocation>> fromTypeToRelatedMethods;
	public final Map<String, Map<String, MethodParameter>> fromTypeToRelatedMethodParameters;
	
	public PropertiesMap(String pathToXmlModel){
		this.pathToXmlModel = pathToXmlModel;
		migrationProp = readProperties(this.pathToXmlModel);
		microservices = getMapFromNameToMicorservice(migrationProp);
		fromTypeToRelatedAtributeTypes = getMapFromTypeToRelatedAtributeTypes(migrationProp);
		fromTypeToRelatedMethods = getMapFromTypeToRelatedMethod(migrationProp);
		fromTypeToRelatedMethodParameters = getMapFromTypeToRelatedMethodParameter(migrationProp);
	}

	public boolean serchingForTypeForMicroservice(String typeQualifiedName){
		return (microservices.containsKey(typeQualifiedName))? true: false;
	}
	
	public String getTypesMicroserviceName(String typeQualifiedName){
		return microservices.get(typeQualifiedName).getName();
	}
	
	public boolean serchingForTypeForAtribute(String typeQualifiedName){
		return (fromTypeToRelatedAtributeTypes.containsKey(typeQualifiedName))? true: false;
	}
	
	public boolean serchingForFieldForAtribute(String typeQualifiedName, String atributeTypeQualifiedName){
		return (fromTypeToRelatedAtributeTypes.get(typeQualifiedName)
				.containsKey(atributeTypeQualifiedName))? true: false;
	}
	
	
	public boolean serchingForTypeForMethod(String typeQualifiedName){
		return (fromTypeToRelatedMethods.containsKey(typeQualifiedName))? true: false;
	}
	
	public boolean serchingForMethodForMethod(String typeQualifiedName, String methodSignature){
		return (fromTypeToRelatedMethods.get(typeQualifiedName)
				.containsKey(methodSignature))? true: false;
	}
	
	public String getMethodsMicroserviceName(String typeQualifiedName, String methodSignature){
		return fromTypeToRelatedMethods.get(typeQualifiedName).get(methodSignature).getTo().getTargetMicroservice();
	}
	
	
	//-----------------------------------------
    //HELPERS
    //-----------------------------------------
	
	
	public static Map<String, Relation> getMapFromNameToToMethodParameter(Migration migrationProp){
		Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
		for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(r instanceof MethodParameter){
				fromAtribute.put(r.getTo().getQualifiedType(), r);
			}
		}
		return fromAtribute;
	}

	public static Migration readProperties(String path) {
		return (Migration)JaxbWriterReader.jaxbReader(Migration.class, path);
	}

	public static Map<String, Microservice> getMapFromNameToMicorservice(Migration migrationProp){
		Map<String, Microservice> microservices = new HashMap<String, Microservice>();
		for(Microservice m: migrationProp.getMicroservices().getMicroservice()){
			for(String type: m.getQualifiedType()){
				microservices.put(type, m);
			}
		}
		return microservices;
	}

	public static Map<String, Map<String, Attribute>> getMapFromTypeToRelatedAtributeTypes(Migration migrationProp){
		Map<String, Map<String, Attribute>> mapAtributes= new HashMap<String, Map<String, Attribute>>();
		
		for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(r instanceof Attribute){
				Attribute attribute = (Attribute)r;
				String fromType = r.getFrom().getQualifiedType();
				String toType = r.getTo().getQualifiedType();
				if(mapAtributes.containsKey(fromType)){
					mapAtributes.get(fromType).put(fromType, attribute);
				}
				else{
					HashMap<String, Attribute> map = new HashMap<String, Attribute>();
					map.put(toType, attribute);
					mapAtributes.put(fromType, map);
				}
			}
		}
		return mapAtributes;
	}

	public static Map<String, Map<String, MethodInvocation>> getMapFromTypeToRelatedMethod(
			Migration migrationProp)
	{
		Map<String, Map<String, MethodInvocation>> mapMethods = new HashMap<String, Map<String, MethodInvocation>>();
		for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(r instanceof MethodInvocation){
				MethodInvocation method = (MethodInvocation)r;
				String fromType = method.getFrom().getQualifiedType();
				
				StringBuilder sb = new StringBuilder();
				for(Parameter p: method.getMethod().getParameters().getParameter()){
					sb.append(p.getParamType());
					sb.append(",");
				}
				String params = (sb.length() > 0)? sb.substring(0, sb.length()-1): sb.toString();
				
				String toMethodSignature = method.getMethod().getReturnType() + " " 
				+ method.getMethod().getMethodName() + 
				"(" + params +")";
				
				Map<String,String> atributes = new HashMap<String, String>();
				atributes.put("toQualifiedType", method.getTo().getQualifiedType());
				atributes.put("toMicroservice", method.getTo().getTargetMicroservice());
				if(mapMethods.containsKey(fromType)){
					mapMethods.get(fromType).put(toMethodSignature, method);
				}
				else{
					Map<String, MethodInvocation> mapToMethod = 
							new HashMap<String, MethodInvocation>();
					mapToMethod.put(toMethodSignature, method);
					mapMethods.put(fromType, mapToMethod);
				}
				
			}
		}
		return mapMethods;
	}

	public static Map<String, Map<String, MethodParameter>> getMapFromTypeToRelatedMethodParameter(Migration migrationProp){
		Map<String, Map<String, MethodParameter>> mapMethods = new HashMap<String, Map<String, MethodParameter>>();
		for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
			if(r instanceof MethodParameter){
				MethodParameter method = (MethodParameter)r;
				String fromType = method.getFrom().getQualifiedType();
				
				StringBuilder sb = new StringBuilder();
				for(Parameter p: method.getMethod().getParameters().getParameter()){
					sb.append(p.getParamType());
					sb.append(",");
				}
				String params = (sb.length() > 0)? sb.substring(0, sb.length()-1): sb.toString();
				
				String toMethodSignature = method.getMethod().getReturnType() + " " 
				+ method.getMethod().getMethodName() + 
				"(" + params +")";
				
				Map<String,String> atributes = new HashMap<String, String>();
				atributes.put("toQualifiedType", method.getTo().getQualifiedType());
				atributes.put("toMicroservice", method.getTo().getTargetMicroservice());
				if(mapMethods.containsKey(fromType)){
					mapMethods.get(fromType).put(toMethodSignature, method);
				}
				else{
					Map<String, MethodParameter> mapToMethod = 
							new HashMap<String, MethodParameter>();
					mapToMethod.put(toMethodSignature, method);
					mapMethods.put(fromType, mapToMethod);
				}
				
			}
		}
		return mapMethods;
	}
	
	
	
}
