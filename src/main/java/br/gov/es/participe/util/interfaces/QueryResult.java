package br.gov.es.participe.util.interfaces;

import java.lang.annotation.*;

/**
 * Annotation to be applied to interfaces and classes onto which arbitrary Cypher query results are to be mapped.
 *
 * @author Adam George
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface QueryResult {

    // no annotation properties needed

}