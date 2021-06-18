package net.square.intect.checks.objectable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();
    String type();
    String description();
    int maxVL();
    boolean experimental() default false;
    boolean bukkit() default false;
}
