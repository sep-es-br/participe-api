package br.gov.es.participe;

import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class BaseTest {

    @Container
    protected static final Neo4jContainer databaseServer = new Neo4jContainer<>().withoutAuthentication();
}
