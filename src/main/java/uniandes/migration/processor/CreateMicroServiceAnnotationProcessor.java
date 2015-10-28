package uniandes.migration.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import uniandes.migration.enumeration.HttpMethod;
import uniandes.migration.util.PropertiesMap;
import uniandes.migration.generated.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by carlos on 9/23/15.
 */
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtType<?>>{

	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	
	private Map<String, CtAnnotationType<?>> annotations;
	
	private static Map<String, CtType<?>> requiresMicroserviceAnnotation;
	private static Map<String,CtFieldReference<?>> atributesRequiringConsumeAnotation;
	private static Map<String,CtMethod<?>> methodsRequiringConsumeAnotation;

    private PropertiesMap propertiesMap;
    
    @Override
    public void init() {
        super.init();
        propertiesMap = new PropertiesMap(PATH_TO_MODEL_XML);
        
        requiresMicroserviceAnnotation = new HashMap();
        atributesRequiringConsumeAnotation = new HashMap<String,CtFieldReference<?>>();
        methodsRequiringConsumeAnotation = new HashMap<String, CtMethod<?>>();
        
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
    }


	public void process(CtType<?> ctType) {    	
    	
		String typeQualifiedName = ctType.getQualifiedName();
		//If microservice
		if(propertiesMap.serchingForTypeForMicroservice(typeQualifiedName)){
			requiresMicroserviceAnnotation.put(ctType.getQualifiedName(),ctType);
		}

		//If type (class) contains required atribute
		if(propertiesMap.serchingForTypeForAtribute(typeQualifiedName)){
			for(CtFieldReference<?> fr: ctType.getAllFields()){
				if(propertiesMap.serchingForFieldForAtribute(typeQualifiedName, fr.getType().getQualifiedName())){
					atributesRequiringConsumeAnotation.put(fr.getQualifiedName(), fr);
				}
			}
		}

		//If type (class) contains required method
		if(propertiesMap.serchingForTypeForMethod(typeQualifiedName)){
			for(Object o: ctType.getAllMethods()){
				CtMethod<?> m = (CtMethod)o;
				if(propertiesMap.serchingForMethodForMethod(typeQualifiedName, m.getSignature())){
					methodsRequiringConsumeAnotation.put(m.getSignature(), m);
				}
			}
		}

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
        
        //Annotate methods
        for(String t: methodsRequiringConsumeAnotation.keySet()){
        	annotateMethodWithConsumes(methodsRequiringConsumeAnotation.get(t));
        }
        
    }
    
    //-----------------------------------------
    //HELPERS
    //-----------------------------------------
    
    private void annotateWithMicroservice(CtType<?> ctType){
    	String typeQualifiedName = ctType.getQualifiedName();
    	
    	//Create annotation
		CtAnnotationType<?> microserviceType = annotations.get("uniandes.migration.annotation.Microservice");
    	CtTypeReference ctAnnotationType = microserviceType.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        ctAnnotation.addValue("name", propertiesMap.getTypesMicroserviceName(typeQualifiedName));
		
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctType.getReference().getDeclaration().getAnnotations());
        list.add(ctAnnotation);
        ctType.getReference().getDeclaration().setAnnotations(list);
        
        System.out.println("[INFO] Annotate with microservice:  " + typeQualifiedName);
    }
    
    private void annotateAtributeWithConsumes(CtFieldReference<?> ctFieldReference) {
    	String mkey = ctFieldReference.getQualifiedName();
    	
    	//Create annotation Consumes
		CtAnnotationType<?> consumes = annotations.get("uniandes.migration.annotation.Consumes");
    	CtTypeReference ctAnnotationType = consumes.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        
        //Create annotation Consume
        CtAnnotationType<?> consume = annotations.get("uniandes.migration.annotation.Consume");
    	CtTypeReference ctAnnotationTypeConsume = consume.getReference();
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


    private void annotateMethodWithConsumes(CtMethod<?> ctMethod) {
    	String methodSignature = ctMethod.getSignature();
    	
    	//Create annotation Consumes
		CtAnnotationType<?> consumes = annotations.get("uniandes.migration.annotation.Consumes");
    	CtTypeReference ctAnnotationType = consumes.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        
        //Create annotation Consume
        CtAnnotationType<?> consume = annotations.get("uniandes.migration.annotation.Consume");
    	CtTypeReference ctAnnotationTypeConsume = consume.getReference();
        CtAnnotation<?> ctAnnotationConsume = this.getFactory().Core().createAnnotation();
        ctAnnotationConsume.setAnnotationType(ctAnnotationTypeConsume);
        
        String parentQulifiedname = ((CtType<?>)ctMethod.getParent()).getQualifiedName();
        
        String toMicroservice = propertiesMap.getMethodsMicroserviceName(parentQulifiedname, methodSignature);
        
        ctAnnotationConsume.addValue("microservice", toMicroservice);
        
        if(!ctMethod.getType().getSimpleName().equals("void") && methodOnlyHasPrimitiveValues(ctMethod)){
        	ctAnnotationConsume.addValue("method", HttpMethod.GET);
        }
        else{
        	ctAnnotationConsume.addValue("method", HttpMethod.POST);
        }
        
        
        
        CtAnnotation []consumeArray = new CtAnnotation[] {ctAnnotationConsume};
        ctAnnotation.addValue("value", consumeArray);
        
		
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctMethod.getAnnotations());
        list.add(ctAnnotation);
        ctMethod.setAnnotations(list);
        
        System.out.println("[INFO] Annotate with Consumes:  " + methodSignature);
		
	}


    public boolean methodOnlyHasPrimitiveValues(CtMethod ctMethod){
    	boolean primitive = true;
    	for(Object o:ctMethod.getParameters()){
    		CtParameter<?> p = (CtParameter)o;
    		String signature = p.getType().getQualifiedName();
    		if(!signature.equals("long") &&
    		   !signature.equals("int") &&
    		   !signature.equals("char") &&
    		   !signature.equals("byte") &&
    		   !signature.equals("double") &&
    		   !signature.equals("float") &&
    		   !signature.equals("java.lang.String")
    				)
    		{
    			primitive = false;
    			break;
    		}
    	}
    	return primitive;
    }


    //-----------------------------------------
    //GETTERS AND SETTERS
    //-----------------------------------------

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
    
	public static Map<String, CtMethod<?>> getMethodsRequiringConsumeAnotation() {
		return methodsRequiringConsumeAnotation;
	}

	public static void setMethodsRequiringConsumeAnotation(
			Map<String, CtMethod<?>> methodsRequiringConsumeAnotation) {
		CreateMicroServiceAnnotationProcessor.methodsRequiringConsumeAnotation = methodsRequiringConsumeAnotation;
	}
    
    
}
