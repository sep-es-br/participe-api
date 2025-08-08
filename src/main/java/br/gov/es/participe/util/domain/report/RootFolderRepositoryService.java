/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
        
        String fileName = Paths.get(uri).getFileName().toString();  // resolve C:\abc\xyz.jasper -> xyz.jasper
        Path filePath = this.root.resolve(fileName);

        if (!Files.exists(filePath)) {
            Path imgPath = this.root.resolve("imgs").resolve(fileName);
            if (Files.exists(imgPath)) {
                filePath = imgPath;
            } else {
                Path imgAbsolutePath = Paths.get(uri);
                if (Files.exists(imgAbsolutePath)) {
                    filePath = imgAbsolutePath;
                } else {
                    Logger.getGlobal().warning("File not found: " + fileName);
                    return null;
                }
            }
        }
               
        if(InputStreamResource.class.isAssignableFrom(resourceType)) {
            try {
                if (Paths.get(uri).equals(filePath) && (fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".jpg"))) {
                    BufferedImage original = ImageIO.read(filePath.toFile());

                    // Converte para RGB se necessário
                    BufferedImage converted = new BufferedImage(
                        original.getWidth(), 
                        original.getHeight(), 
                        BufferedImage.TYPE_INT_ARGB
                    );
                    Graphics2D g2d = converted.createGraphics();
                    g2d.drawImage(original, 0, 0, null);
                    g2d.dispose();
                    
                    BufferedImage editedImage = editImage(converted, 1f);
                    
                    // Salva em memória
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(editedImage, "png", baos);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                    InputStreamResource resource = new InputStreamResource();
                    resource.setInputStream(bais);
                    return resourceType.cast(resource);
                }
                
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

    public BufferedImage editImage(BufferedImage original, float alphaFactor) {
        // Clonar imagem com suporte a canal alfa (ARGB)
        BufferedImage result = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int rgba = original.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                int red   = (rgba >> 16) & 0xff;
                int green = (rgba >> 8)  & 0xff;
                int blue  = rgba & 0xff;

                // Aumenta a opacidade (alpha mais próximo de 255)
                alpha = Math.min(255, (int)(alpha * alphaFactor));
                
                red   = 255;
                green = 255;
                blue  = 255;

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, newPixel);
            }
        }

        return result;
    }

    
    
    
    
    
}
