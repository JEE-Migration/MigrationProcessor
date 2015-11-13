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
import uniandes.migration.util.IPropertiesMap;
import uniandes.migration.util.MethodKey;
import uniandes.migration.util.PropertiesMap;

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

//	public static final String PATH_TO_MODEL_XML = "./src/main/resources/kdmResult.xml";
	public static final String PATH_TO_MODEL_XML = "./src/main/resources/marketplace.xml";
	
	
	private Map<String, CtAnnotationType<?>> annotations;
	
	public static Map<String, CtType<?>> microservices;
	public static Map<MethodKey,CtMethod<?>> consumeMethods;
	public static Map<MethodKey,CtMethod<?>> producesMethods;

    private IPropertiesMap propertiesMap;
    
    @Override
    public void init() {
        super.init();
        propertiesMap = new PropertiesMap(PATH_TO_MODEL_XML);
        
        microservices = new HashMap();
        consumeMethods = new HashMap<MethodKey, CtMethod<?>>();
        producesMethods = new HashMap<MethodKey, CtMethod<?>>();
        
        GetAnnotationProcessor.readAnnotations(GetAnnotationProcessor.ANNOTATIONS_PATH);
        annotations =  GetAnnotationProcessor.getAnnotations();
    }


	public void process(CtType<?> ctType) {    	
    	
		String typeQualifiedName = ctType.getQualifiedName();
		//If microservice
		if(propertiesMap.searchingForMicroserviceType(typeQualifiedName)){
			microservices.put(ctType.getQualifiedName(),ctType);
			for(Object o: ctType.getAllMethods()){
				CtMethod<?> m = (CtMethod)o;
				producesMethods.put(new MethodKey(typeQualifiedName, m.getSignature()), m);
			}
		}

		//If type (class) contains invocated method
		if(propertiesMap.searchingForTypeForMIFromMethod(typeQualifiedName)){
			for(Object o: ctType.getAllMethods()){
				CtMethod<?> m = (CtMethod)o;
				if(propertiesMap.searchingForMIFromMethod(typeQualifiedName, m.getSignature())){
					consumeMethods.put(new MethodKey(typeQualifiedName, m.getSignature()), m);
				}
			}
		}

    }

    @Override
    public void processingDone() {
        super.processingDone();
        
        //Annotate microservices
        for(String t: microservices.keySet()){
        	annotateWithMicroservice(microservices.get(t));
        }
        
        //Annotate methods
        for(MethodKey t: consumeMethods.keySet()){
        	annotateMethodWithConsumes(t, consumeMethods.get(t));
        }
        
        int n = 0;
        for(MethodKey t: producesMethods.keySet()){
        	annotateMethodWithProvides(t, producesMethods.get(t));
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
        ctAnnotation.addValue("name", propertiesMap.getTypesMicroservice(typeQualifiedName));
		
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctType.getReference().getDeclaration().getAnnotations());
        list.add(ctAnnotation);
        ctType.getReference().getDeclaration().setAnnotations(list);
        
        System.out.println("[INFO] Annotate with microservice:  " + typeQualifiedName);
    }
    
   
    private void annotateMethodWithConsumes(MethodKey key, CtMethod<?> ctMethod) {
    	Set<MethodKey> toMethods = propertiesMap.getToMethods(key);
    	
    	//Create annotation Consumes
		CtAnnotationType<?> consumes = annotations.get("uniandes.migration.annotation.Consumes");
    	CtTypeReference ctAnnotationType = consumes.getReference();
        CtAnnotation<?> ctAnnotation = this.getFactory().Core().createAnnotation();
        ctAnnotation.setAnnotationType(ctAnnotationType);
        
        //Create annotation Consume
        CtAnnotationType<?> consume = annotations.get("uniandes.migration.annotation.Consume");
        List<CtAnnotation> consumeList = new ArrayList<CtAnnotation>();
        for(MethodKey tm: toMethods){
        	CtTypeReference ctAnnotationTypeConsume = consume.getReference();
            CtAnnotation<?> ctAnnotationConsume = this.getFactory().Core().createAnnotation();
            ctAnnotationConsume.setAnnotationType(ctAnnotationTypeConsume);	
 
            
            ctAnnotationConsume.addValue("microservice", 
            		propertiesMap.getTypesMicroservice(tm.getType()));
           
           
            String [] parametertypes = getMethodParamterTypes(ctMethod);
            String returnType = getReturnType(ctMethod);
            ctAnnotationConsume.addValue("parameterTypes", parametertypes);
            ctAnnotationConsume.addValue("returnValue", returnType);
            
            ctAnnotationConsume.addValue("method", getHttpMethod(parametertypes, returnType));
            
            ctAnnotationConsume.addValue("path", getPath(tm));
            
            consumeList.add(ctAnnotationConsume);
        }
        
        ctAnnotation.addValue("value", consumeList.toArray());
        
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctMethod.getAnnotations());
        list.add(ctAnnotation);
        ctMethod.setAnnotations(list);
        
        System.out.println("[INFO] Annotate with Consumes:  " + key);
		
	}


   


	private void annotateMethodWithProvides(MethodKey fromMethodKey, CtMethod<?> ctMethod) {
    	
    	//Create annotation Produce
        CtAnnotationType<?> provides = annotations.get("uniandes.migration.annotation.Provides");
       
        CtTypeReference ctAnnotationTypeProvides = provides.getReference();
        CtAnnotation<?> ctAnnotationProvides = this.getFactory().Core().createAnnotation();
        ctAnnotationProvides.setAnnotationType(ctAnnotationTypeProvides);	


        ctAnnotationProvides.addValue("microservice", propertiesMap.getTypesMicroservice(fromMethodKey.getType()));


        String [] parametertypes = getMethodParamterTypes(ctMethod);
        String returnType = getReturnType(ctMethod);
        ctAnnotationProvides.addValue("parameterTypes", parametertypes);
        ctAnnotationProvides.addValue("returnValue", returnType);

        ctAnnotationProvides.addValue("method", getHttpMethod(parametertypes, returnType));

        ctAnnotationProvides.addValue("path", getPath(fromMethodKey));
        
        //Add anotation
		List<CtAnnotation<? extends Annotation>> list = new ArrayList<CtAnnotation<? extends Annotation>>();
        list.addAll(ctMethod.getAnnotations());
        list.add(ctAnnotationProvides);
        ctMethod.setAnnotations(list);
        
        System.out.println("[INFO] Annotate with Provides:  " + fromMethodKey);
		
	}
    
    public HttpMethod getHttpMethod(String[] parametertypes, String retrunType){
    	if(!retrunType.equals("void") && onlyPrimitivaValues(parametertypes)){
    		return HttpMethod.GET;
    	}
    	else{
    		return HttpMethod.POST;
    	}
    }
    
    public String[] getMethodParamterTypes(CtMethod ctMethod){
    	List <String> parameterTypes = new ArrayList<String>();
    	for(Object o:ctMethod.getParameters()){
    		CtParameter<?> p = (CtParameter)o;
    		String type = p.getType().getQualifiedName();
    		parameterTypes.add(type);
    	}
    	return (String [])parameterTypes.toArray(new String[parameterTypes.size()]);
    }
    
    public String getReturnType(CtMethod ctMethod){
    	return ctMethod.getType().getQualifiedName();
    }
    
    private Object getPath(MethodKey tm) {
    	String s = tm.getSignature().substring(tm.getSignature().lastIndexOf(" ")+1, tm.getSignature().lastIndexOf("("));
		return "/" + s;
	}

	public boolean onlyPrimitivaValues(String[] parameterTypes){
    	boolean primitive = true;
    	for(String p:parameterTypes){
    		if(!p.equals("long") &&
    		   !p.equals("int") &&
    		   !p.equals("char") &&
    		   !p.equals("byte") &&
    		   !p.equals("double") &&
    		   !p.equals("float") &&
    		   !p.equals("java.lang.String")
    				)
    		{
    			primitive = false;
    			break;
    		}
    	}
    	return primitive;
    }
    
}
