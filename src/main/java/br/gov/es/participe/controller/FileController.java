package br.gov.es.participe.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.gov.es.participe.controller.dto.FileDto;
import br.gov.es.participe.service.FileService;
import br.gov.es.participe.service.PersonService;

@RestController
@CrossOrigin
@RequestMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UrlResource> getImagem(@PathVariable(value = "id") Long idImagem) throws IOException {
        UrlResource imagem = fileService.getFile(idImagem);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaTypeFactory
                        .getMediaType(imagem)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(imagem);
    }

    @SuppressWarnings("rawtypes")
    @Transactional

    @PostMapping(headers = { "Content-Type=multipart/form-data" }, value = "/upload")
    public ResponseEntity upload(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        FileDto fileDto = fileService.save(file);
        return ResponseEntity.status(HttpStatus.OK).body(fileDto);
    }
 
    @SuppressWarnings("rawtypes")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity delete(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable(value = "id") Long idLocalidade) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        fileService.delete(idLocalidade);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
