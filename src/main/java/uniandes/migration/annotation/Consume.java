package uniandes.migration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by carlos on 9/23/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Consume {
    String serviceName() default "";

}
