package uniandes.migration.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import uniandes.migration.annotation.Microservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by carlos on 9/23/15.
 */
public class CreateMicroServiceAnnotationProcessor extends AbstractProcessor<CtType<?>>{

    Properties props;

    @Override
    public void init() {
        super.init();
        props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/resources/microservice.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(CtType<?> ctType) {
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
}
