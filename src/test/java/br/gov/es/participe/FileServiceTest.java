package br.gov.es.participe;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.participe.controller.FileController;
import br.gov.es.participe.controller.dto.FileDto;
import br.gov.es.participe.repository.FileRepository;
import br.gov.es.participe.service.FileService;

@Testcontainers
@SpringBootTest
class FileServiceTest extends BaseTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileController fileController;


    @Autowired
    private FileRepository fileRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearFiles() {
        fileRepository.deleteAll();
    }

    @Test
    public void shouldCreateFile() throws IOException {
        MultipartFile file = createFileParamDto();

        ResponseEntity response = fileController.upload(file);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindFile() throws IOException {
        MultipartFile file = createFileParamDto();
        FileDto fileDtoParam = fileService.save(file);

        UrlResource responseEntity =  fileController.getImagem(fileDtoParam.getId()).getBody();

        Assert.assertNotNull(responseEntity);
        Assert.assertNotNull(responseEntity.getFilename());
    }


    @Test
    public void shouldDeleteFile() throws IOException {
        MultipartFile file = createFileParamDto();
        FileDto fileDto = fileService.save(file);

        fileController.delete(fileDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> fileController.getImagem(fileDto.getId()));
    }

    private MultipartFile createFileParamDto() throws IOException {
        InputStream is = getClass().getResourceAsStream("/teste.pdf");
        return  new MockMultipartFile("teste", "teste.pdf", MediaType.APPLICATION_PDF_VALUE, is);
    }
}