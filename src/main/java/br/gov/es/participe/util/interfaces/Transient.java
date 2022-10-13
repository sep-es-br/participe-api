package br.gov.es.participe.util.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By default all domain entity types will be persisted unless they are
 * annotated with @Transient, or are non-annotated abstract classes.
 * This annotation can be placed on types, fields and methods
 * and the OGM will ignore any object or object reference with
 * the annotation
 *
 * @author Mark Angrish
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
@Inherited
public @interface Transient {

}
