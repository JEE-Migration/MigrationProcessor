package uniandes.migration.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import uniandes.migration.annotation.Microservice;
import generated.Migration;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by carlos on 9/23/15.
 */
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtType<?>>{

	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	
	private Migration migrationProp;
	Map<String, CtAnnotationType<?>> annotations;
	
	
    Properties props;

    @Override
    public void init() {
        super.init();
        migrationProp = readProperties(PATH_TO_MODEL_XML);
        AnnotationContainer.readAnnotations(AnnotationContainer.ANNOTATIONS_PATH);
        annotations =  AnnotationContainer.getAnnotations();
        
        props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/resources/microservice.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(CtType<?> ctType) {
//        String s = migrationProp.getMicroservices().getMicroservice().get(0).getQualifiedType();
//        System.out.println(s);
    	for(Object key : props.keySet()){
            String stringKey = key.toString();
            if(stringKey.equals(ctType.getQualifiedName())){
                System.out.println("[INFO] Found type ready to receive a new annotation "+stringKey);
                List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
                list.addAll(ctType.getReference().getDeclaration().getAnnotations());
                list.add(createAnnotation());
                ctType.getReference().getDeclaration().setAnnotations(list);
            }

        }
    }

    private CtAnnotation<?> createAnnotation(){
        CtTypeReference<Annotation> ctAnnotationType = this.getFactory().Core().createTypeReference();
        ctAnnotationType.setSimpleName("uniandes.migration.annotation.Microservice");
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);

        return ctAnnotation;
    }


    @Override
    public void processingDone() {
        super.processingDone();
    }
    
    //-----------------------------------------
    //HELPERS
    //-----------------------------------------
    
    
    public static Migration readProperties(String path) {
    	return (Migration)JaxbWriterReader.jaxbReader(Migration.class, path);
    }
    
    
    
}
