package com.lqtemple.android.lqbookreader.annotation;

/**
 * Created by sundxing on 16/12/11.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {
    int value() default -1;
    String tag() default "";
    boolean click() default false;
}
