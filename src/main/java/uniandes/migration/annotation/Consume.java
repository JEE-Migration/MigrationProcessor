package uniandes.migration.annotation;

import uniandes.migration.enumeration.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by carlos on 9/23/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Consume {
	
	String microservice();
	String path() default "";
    HttpMethod method();
    String [] parameterTypes();
    String returnValue();

    //?????
    String injectionName();
    
}
