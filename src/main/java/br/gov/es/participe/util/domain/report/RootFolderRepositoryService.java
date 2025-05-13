/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.DefaultRepositoryService;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.Resource;

/**
 *
 * @author gean.carneiro
 */
public class RootFolderRepositoryService extends DefaultRepositoryService {
    
    private final Path root;
    
    public RootFolderRepositoryService(JasperReportsContext jasperReportsContext, Path root) {
        super(jasperReportsContext);
        this.root = root;
    }

    @Override
    public <K extends Resource> K getResource(RepositoryContext context, String uri, Class<K> resourceType) {
        
        Path filePath = this.root.resolve(uri);
        
        Logger.getGlobal().log(Level.INFO, uri);
        Logger.getGlobal().log(Level.INFO, resourceType.getSimpleName());
        Logger.getGlobal().log(Level.INFO, String.valueOf(InputStreamResource.class.isAssignableFrom(resourceType)));
        
        
        if(InputStreamResource.class.isAssignableFrom(resourceType)) {
            try {
                InputStreamResource resource = new InputStreamResource();
                resource.setInputStream(new FileInputStream(filePath.toFile()));
                return resourceType.cast(resource);
            } catch (IOException  ex) {
                Logger.getLogger(RootFolderRepositoryService.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } 
        }
        
        return super.getResource(context, uri, resourceType); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
    
    
    
}
