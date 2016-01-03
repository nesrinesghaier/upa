package net.vpc.upa.config;

import net.vpc.upa.ObjectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vpc on 7/25/15.
 */
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface OnDrop {
    String name() default "";

    ObjectType[] types() default {};

    boolean trackSystemObjects() default false;

    boolean before() default false;

    boolean after() default true;

    Config config() default @Config();
}
