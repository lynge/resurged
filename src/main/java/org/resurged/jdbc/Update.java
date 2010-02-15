package org.resurged.jdbc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {
	String value() default "";
	String sql() default "";
	GeneratedKeys keys() default GeneratedKeys.NO_KEYS_RETURNED;
}