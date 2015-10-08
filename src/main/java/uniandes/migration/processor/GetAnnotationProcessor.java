package uniandes.migration.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

public class GetAnnotationProcessor extends AbstractProcessor<CtAnnotationType<?>>{

	private final Map<String, CtAnnotationType<?>> annotations = new HashMap<String, CtAnnotationType<?>>();
	
	@Override
    public void init() {
        super.init();
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
        AnnotationContainer.setAnnotations(annotations);
    }

}
