package uniandes.migration.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import uniandes.migration.invoker.Invoker;

public class GetAnnotationProcessor extends AbstractProcessor<CtAnnotationType<?>>{

	private static Map<String, CtAnnotationType<?>> annotations;
	public static final String PROCESSOR = "uniandes.migration.processor.GetAnnotationProcessor";
	public static String ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/annotation";
	
	@Override
    public void init() {
        super.init();
        annotations = new HashMap<String, CtAnnotationType<?>>();
    }
	
	public void process(CtAnnotationType<?> element) {
//		System.out.println(element);
		String key = element.getQualifiedName();
		annotations.put(key, element);
		
	}
	
	@Override
    public void processingDone() {
        super.processingDone();
        System.out.println("Found " + annotations.size() + " AnnotationTypes:");
        for(String key: annotations.keySet()){
        	System.out.println("\t" + key);
        }
        GetAnnotationProcessor.setAnnotations(annotations);
    }
	
	
	public static void setAnnotations(Map<String, CtAnnotationType<?>> annot){
		annotations = annot;
	}
	
	public static Map<String, CtAnnotationType<?>> getAnnotations(){
		return annotations;
	}
	
	/**
	 * Initializes the annotations
	 */
	public static void readAnnotations(String annotationPath){
		Invoker invoker = new Invoker();
		invoker.invokeSpoon(PROCESSOR, annotationPath);
	}
	

}
