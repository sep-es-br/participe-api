package br.gov.es.participe.util.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the domain entity representing the end node of
 * a relationship in the graph, and, along with @{@link StartNode}
 * is a mandatory annotation on any domain entity that is annotated
 * with @{@link RelationshipEntity}
 *
 * @author Michal Bachman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EndNode {

}