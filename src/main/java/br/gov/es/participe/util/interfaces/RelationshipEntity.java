package br.gov.es.participe.util.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Value;

/**
 * Identifies a domain entity as being backed by a relationship in the graph.
 * This annotation is always needed for relationship-backed entities.
 * The type attribute supplies the relationship type in the graph.
 *
 * @author Michal Bachman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RelationshipEntity {

    String TYPE = "type";

    @Value(TYPE)
    String value() default "";

    String type() default "";
}