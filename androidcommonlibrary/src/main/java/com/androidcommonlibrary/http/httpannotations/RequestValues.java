package com.androidcommonlibrary.http.httpannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by admin1 on 3/8/17.
 */

@Target({ElementType.METHOD,ElementType.LOCAL_VARIABLE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestValues {
    String[] values() default "";
}

