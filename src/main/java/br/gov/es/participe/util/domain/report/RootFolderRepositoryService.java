/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.repo.InputStreamResource;

import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.repo.ResourceInfo;

/**
 *
 * @author gean.carneiro
 */
public class RootFolderRepositoryService implements RepositoryService {
    private final File pastaBase;

    public RootFolderRepositoryService(String pastaBase) {
        this.pastaBase = new File(pastaBase);
    }
    public RootFolderRepositoryService(File pastaBase) {
        this.pastaBase = pastaBase;
    }

    @Override
    public <K extends Resource> K getResource(RepositoryContext context, String uri, Class<K> resourceType) {
        try {
            File file = new File(pastaBase, uri);
            if (!file.exists() || !file.isFile()) {
                return null;
            }

            if (InputStreamResource.class.isAssignableFrom(resourceType)) {
                InputStreamResource resource = new InputStreamResource();
                resource.setInputStream(new FileInputStream(file));
                return resourceType.cast(resource);
            }

            return null; // outros tipos não tratados aqui
        } catch (Exception e) {
            throw new JRRuntimeException("Erro ao carregar recurso: " + uri, e);
        }
    }

    // Métodos obrigatórios da interface, mas que você não precisa implementar agora
    @Override
    public Resource getResource(String uri) {
        return null;
    }

    @Override
    public <K extends Resource> K getResource(String uri, Class<K> resourceType) {
        return null;
    }

    @Override
    public void saveResource(String uri, Resource resource) {
        // não usado
    }

    @Override
    public ResourceInfo getResourceInfo(RepositoryContext context, String location) {
        return null;
    }
}
