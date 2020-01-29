package br.gov.es.participe.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.es.participe.controller.dto.FileDto;
import br.gov.es.participe.service.FileService;

@RestController
@CrossOrigin
@RequestMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UrlResource> getImagem(@PathVariable(value = "id") Long idImagem) throws IOException {
        UrlResource imagem = fileService.getFile(idImagem);
        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaTypeFactory
                              .getMediaType(imagem)
                              .orElse(MediaType.APPLICATION_OCTET_STREAM))
                             .body(imagem);
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) throws IOException {
        FileDto fileDto = fileService.save(file);
        return ResponseEntity.status(HttpStatus.OK).body(fileDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") Long idLocalidade) {
        fileService.delete(idLocalidade);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
