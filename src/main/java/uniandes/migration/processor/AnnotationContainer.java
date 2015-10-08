package uniandes.migration.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotationType;

public class AnnotationContainer {

	private static final String PROCESSOR = "uniandes.migration.processor.GetAnnotationProcessor";
	public static String ANNOTATIONS_PATH = "./src/main/java/uniandes/migration/annotation";
	
	
	private static Map<String, CtAnnotationType<?>> annotations;
	
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
		// Invoke spoon processor for methods
        String[] spoonArgs = new String[6];
        spoonArgs[0] = "-i";
        spoonArgs[1] = annotationPath;
        spoonArgs[2] = "-p";
        spoonArgs[3] = PROCESSOR;
        spoonArgs[4] = "--compliance";
        spoonArgs[5] = "7";
        try {
            Launcher.main(spoonArgs);
        } catch (Exception e) {
            System.err.println("Error while executing spoon launcher "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
	}
	
}
