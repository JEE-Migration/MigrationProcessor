package uniandes.migration.processor;

import java.util.HashMap;
import java.util.Map;

import spoon.reflect.declaration.CtAnnotationType;

public class AnnotationContainer {

	private static Map<String, CtAnnotationType<?>> annotations;
	
	public static void setAnnotations(Map<String, CtAnnotationType<?>> annot){
		annotations = annot;
	}
	
	public static Map<String, CtAnnotationType<?>> getAnnotations(){
		return annotations;
	}
	
}
