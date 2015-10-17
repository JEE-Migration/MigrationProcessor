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
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by carlos on 9/23/15.
 */
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtElement>{

	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	
	private Migration migrationProp;
	
	private Map<String, CtAnnotationType<?>> annotations;
	
	private Map<String, Microservice> microservices;
	
	private Map<String, Relation> fromAtribute;
	private Map<String, Relation> fromMethodInvocation;
	private Map<String, Relation> fromMethodParameter;
	
	private Map<String, Relation> toAtribute;
	private Map<String, Relation> toMethodInvocation;
	private Map<String, Relation> toMethodParameter;
	
	private static Map<String, CtType<?>> requiresMicroserviceAnnotation;
	
    Properties props;

    
    @Override
    public void init() {
        super.init();
        requiresMicroserviceAnnotation = new HashMap();
        migrationProp = readProperties(PATH_TO_MODEL_XML);
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
        
        microservices = getMapFromNameToMicorservice(migrationProp);
        
        fromAtribute = getMapFromNameToFromAtribute(migrationProp);
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
    	
    	//If microservice
    	if(ctElem instanceof CtType<?>){
    		CtType ctType = (CtType)ctElem;
    		String mkey = ctType.getQualifiedName();
    		if(microservices.containsKey(mkey)){
        		requiresMicroserviceAnnotation.put(ctType.getQualifiedName(),ctType);
        	}
    	}
    	else if(ctElem instanceof CtMethod){
    		CtMethod ctMethod = (CtMethod) ctElem;
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
        for(String t: requiresMicroserviceAnnotation.keySet()){
        	annotateWithMicroservice(requiresMicroserviceAnnotation.get(t));
        }
        
    }
    
    //-----------------------------------------
    //HELPERS
    //-----------------------------------------
    
    
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
    
    public static Map<String, Relation> getMapFromNameToFromAtribute(Migration migrationProp){
    	Map<String, Relation> fromAtribute = new HashMap<String, Relation>();
    	for(Relation r: migrationProp.getRelationships().getAttributeOrMethodInvocationOrMethodParameter()){
    		if(r instanceof Attribute){
    			fromAtribute.put(r.getFrom().getQualifiedType(), r);
    		}
    	}
    	return fromAtribute;
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
    
    
    
    
    
}
