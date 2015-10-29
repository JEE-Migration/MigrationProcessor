package uniandes.migration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uniandes.migration.enumeration.HttpMethod;

/**
 * Created by carlos on 9/23/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provides {
	String microservice();
	String path() default "";
    HttpMethod method();
    String [] parameterTypes();
    String returnValue();
}
