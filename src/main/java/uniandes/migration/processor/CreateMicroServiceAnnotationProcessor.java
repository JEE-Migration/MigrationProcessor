package uniandes.migration.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import generated.Microservice;
import generated.Migration;

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
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtType<?>>{

	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	
	private Migration migrationProp;
	
	private Map<String, CtAnnotationType<?>> annotations;
	private Map<String, Microservice> microservices;
	
	private static Map<String, CtType<?>> requiresMicroserviceAnnotation;
	
    Properties props;

    
    @Override
    public void init() {
        super.init();
        requiresMicroserviceAnnotation = new HashMap();
        migrationProp = readProperties(PATH_TO_MODEL_XML);
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
        
        microservices = getMapFromNameToMicorservice(migrationProp);;
        
        props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/resources/microservice.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(CtType<?> ctType) {    	
    	String mkey = ctType.getQualifiedName();
    	
    	//If microservice
    	if(microservices.containsKey(mkey)){
    		requiresMicroserviceAnnotation.put(ctType.getQualifiedName(),ctType);
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

    
	public static Map<String, CtType<?>> getRequiresMicroserviceAnnotation() {
		return requiresMicroserviceAnnotation;
	}

	public static void setRequiresMicroserviceAnnotation(
			Map<String, CtType<?>> requiresMicroserviceAnnotation) {
		CreateMicroServiceAnnotationProcessor.requiresMicroserviceAnnotation = requiresMicroserviceAnnotation;
	}
    
    
    
    
    
}
