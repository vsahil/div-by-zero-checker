package org.checkerframework.checker.dividebyzero.qual;

import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

// @DefaultQualifierInHierarchy     // Important to comment this out. 
@SubtypeOf({Top.class}) // back to top 
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})

// Do we need QualifierForLiterals and DefaultFor in this file?

public @interface Nonzero { }
