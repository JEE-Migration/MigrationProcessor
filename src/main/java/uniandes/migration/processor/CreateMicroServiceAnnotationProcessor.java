package uniandes.migration.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtFieldImpl;
import util.JaxbWriterReader;
import generated.Attribute;
import generated.MethodInvocation;
import generated.MethodParameter;
import generated.Microservice;
import generated.Migration;
import generated.Relation;
import generated.Relation.From;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by carlos on 9/23/15.
 */
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtElement>{

	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	
	private Migration migrationProp;
	
	private Map<String, CtAnnotationType<?>> annotations;
	
	private Map<String, Microservice> microservices;
	
	Map<String, Set<String>> fromTypeToRelatedAtributeTypes;
	private Map<String, Relation> fromMethodInvocation;
	private Map<String, Relation> fromMethodParameter;
	
	private Map<String, Relation> toAtribute;
	private Map<String, Relation> toMethodInvocation;
	private Map<String, Relation> toMethodParameter;
	
	private static Map<String, CtType<?>> requiresMicroserviceAnnotation;
	private static Map<String,CtFieldReference<?>> atributesRequiringConsumeAnotation;
	
    Properties props;

    
    @Override
    public void init() {
        super.init();
        requiresMicroserviceAnnotation = new HashMap();
        atributesRequiringConsumeAnotation = new HashMap<String,CtFieldReference<?>>();
        
        migrationProp = readProperties(PATH_TO_MODEL_XML);
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
        
        microservices = getMapFromNameToMicorservice(migrationProp);
        
        fromTypeToRelatedAtributeTypes = getMapFromTypeToRelatedAtributeTypes(migrationProp);
        fromMethodInvocation = getMapFromNameToFromMethodInvocation(migrationProp);
        fromMethodParameter = getMapFromNameToFromMethodParameter(migrationProp);
        
        toAtribute = getMapFromNameToToAtribute(migrationProp);
        toMethodInvocation = getMapFromNameToToMethodInvocation(migrationProp);
        toMethodParameter = getMapFromNameToToMethodParameter(migrationProp);
        
        props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/resources/microservice.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   

	public void process(CtElement ctElem) {    	
    	
    	
    	if(ctElem instanceof CtType<?>){
    		CtType ctType = (CtType)ctElem;
    		String mkey = ctType.getQualifiedName();
    		//If microservice
    		if(microservices.containsKey(mkey)){
        		requiresMicroserviceAnnotation.put(ctType.getQualifiedName(),ctType);
        	}
    		
    		//If type (class) contains required atribute
    		if(fromTypeToRelatedAtributeTypes.containsKey(mkey)){
    			for(CtFieldReference<?> fr: ctType.getAllFields()){
    				if(fromTypeToRelatedAtributeTypes.get(mkey).contains(fr.getType().getQualifiedName())){
    					atributesRequiringConsumeAnotation.put(fr.getQualifiedName(), fr);
    				}
    			}
    		}
    	}
    	else if(ctElem instanceof CtMethod){
//    		CtMethod ctMethod = (CtMethod) ctElem;
//    		String signature = ctElem.getSignature();
//    		String visibility = ctMethod.getVisibility().name();
//    		List<String> params = ctMethod.getParameters();
//    		System.out.println(ctMethod);
    	}
    	else if(ctElem instanceof CtFieldImpl<?>){	
    		CtFieldImpl ctFieldImpl = (CtFieldImpl) ctElem;
    		String mkey = ctFieldImpl.getSignature();
//    		if(fromAtribute.containsKey(mkey)){
        		System.out.println(mkey);			
//    		}
    		
    	}
    	else{
    		
    	}
    	
    	
//    	for(Object key : props.keySet()){
//            String stringKey = key.toString();
//            if(stringKey.equals(ctType.getQualifiedName())){
//                System.out.println("[INFO] Found type ready to receive a new annotation "+stringKey);
//                List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
//                list.addAll(ctType.getReference().getDeclaration().getAnnotations());
//                list.add(createAnnotation());
//                ctType.getReference().getDeclaration().setAnnotations(list);
//            }
//        }
    	
    }

    private CtAnnotation<?> createAnnotation(){
        CtTypeReference<Annotation> ctAnnotationType = this.getFactory().Core().createTypeReference();
        ctAnnotationType.setSimpleName("uniandes.migration.annotation.Microservice");
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);

        return ctAnnotation;
    }

    private void annotateWithMicroservice(CtType<?> ctType){
    	String mkey = ctType.getQualifiedName();
    	
    	//Create annotation
		CtAnnotationType<?> microserviceType = annotations.get("uniandes.migration.annotation.Microservice");
    	CtTypeReference ctAnnotationType = microserviceType.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        ctAnnotation.addValue("name", microservices.get(mkey).getName());
		
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctType.getReference().getDeclaration().getAnnotations());
        list.add(ctAnnotation);
        ctType.getReference().getDeclaration().setAnnotations(list);
        
        System.out.println("[INFO] Annotate with microservice:  " + mkey);
    }

    @Override
    public void processingDone() {
        super.processingDone();
        
        //Annotate microservices
        for(String t: requiresMicroserviceAnnotation.keySet()){
        	annotateWithMicroservice(requiresMicroserviceAnnotation.get(t));
        }
        
        //Annotate atributes
        for(String t: atributesRequiringConsumeAnotation.keySet()){
        	annotateAtributeWithConsumes(atributesRequiringConsumeAnotation.get(t));
        }
        
    }
    
    //-----------------------------------------
    //HELPERS
    //-----------------------------------------
    
    
    private void annotateAtributeWithConsumes(CtFieldReference<?> ctFieldReference) {
    	String mkey = ctFieldReference.getQualifiedName();
    	
    	//Create annotation Consumes
		CtAnnotationType<?> consumes = annotations.get("uniandes.migration.annotation.Consumes");
    	CtTypeReference ctAnnotationType = consumes.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        
        //Create annotation Consume
        CtAnnotationType<?> consume = annotations.get("uniandes.migration.annotation.Consume");
    	CtTypeReference ctAnnotationTypeConsume = consumes.getReference();
        CtAnnotation<?> ctAnnotationConsume = this.getFactory().Core().createAnnotation();
        ctAnnotationConsume.setAnnotationType(ctAnnotationTypeConsume);
        ctAnnotationConsume.addValue("injectionName", ctFieldReference.getSimpleName());
        
        
        CtAnnotation []consumeArray = new CtAnnotation[] {ctAnnotationConsume};
        ctAnnotation.addValue("value", consumeArray);
        
		
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctFieldReference.getDeclaration().getAnnotations());
        list.add(ctAnnotation);
        ctFieldReference.getDeclaration().setAnnotations(list);
        
        System.out.println("[INFO] Annotate with Consumes:  " + mkey);
		
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
    
    public static Map<String, Set<String>> getMapFromTypeToRelatedAtributeTypes(Migration migrationProp){
    	Map<String, Set<String>> mapAtributes = new HashMap<String, Set<String>>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof Attribute){
    			String fromType = r.getFrom().getQualifiedType();
    			String toType = r.getTo().getQualifiedType();
    			if(mapAtributes.containsKey(fromType)){
    				mapAtributes.get(fromType).add(toType);
    			}
    			else{
    				Set<String> toTypes = new HashSet<String>();
    				toTypes.add(toType);
    				mapAtributes.put(fromType, toTypes);
    			}
    		}
    	}
    	return mapAtributes;
    }
    
    public static Map<String, Relation> getMapFromNameToFromMethodInvocation(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof MethodInvocation){
    			fromAtribute.put(r.getFrom().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
    }
    
    public static Map<String, Relation> getMapFromNameToFromMethodParameter(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof MethodParameter){
    			fromAtribute.put(r.getFrom().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
    }
    
    public static Map<String, Relation> getMapFromNameToToAtribute(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof Attribute){
    			fromAtribute.put(r.getTo().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
    }
    
    public static Map<String, Relation> getMapFromNameToToMethodInvocation(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof MethodInvocation){
    			fromAtribute.put(r.getTo().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
    }
    
    public static Map<String, Relation> getMapFromNameToToMethodParameter(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof MethodParameter){
    			fromAtribute.put(r.getTo().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
    }


    
	public static Map<String, CtType<?>> getRequiresMicroserviceAnnotation() {
		return requiresMicroserviceAnnotation;
	}

	public static void setRequiresMicroserviceAnnotation(
			Map<String, CtType<?>> requiresMicroserviceAnnotation) {
		CreateMicroServiceAnnotationProcessor.requiresMicroserviceAnnotation = requiresMicroserviceAnnotation;
	}
    
	public static Map<String, CtFieldReference<?>> getAtributesRequiringConsumeAnotation() {
		return atributesRequiringConsumeAnotation;
	}

	public static void setAtributesRequiringConsumeAnotation(
			Map<String, CtFieldReference<?>> atributesRequiringConsumeAnotation) {
		CreateMicroServiceAnnotationProcessor.atributesRequiringConsumeAnotation = atributesRequiringConsumeAnotation;
	}
    
    
    
    
}
