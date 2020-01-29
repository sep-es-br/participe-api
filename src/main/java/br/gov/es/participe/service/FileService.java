package br.gov.es.participe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.gov.es.participe.configuration.ApplicationProperties;
import br.gov.es.participe.controller.dto.FileDto;
import br.gov.es.participe.repository.FileRepository;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Transactional(readOnly = true)
    public FileDto findById(Long id) {
        br.gov.es.participe.model.File file = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada."));
        return new FileDto(file);
    }

    @Transactional(readOnly = true)
    public br.gov.es.participe.model.File find(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada."));
    }

    public UrlResource getFile(Long id) throws IOException {
        FileDto fileDto = findById(id);
        String caminhoArquivo = applicationProperties.getPathImagens().concat(fileDto.getUrl());
        URI uri = Paths.get(caminhoArquivo).toUri();
        return new UrlResource(uri);
    }

    @Transactional
    public FileDto save(MultipartFile multipartFile) throws IOException {
        br.gov.es.participe.model.File file = new br.gov.es.participe.model.File();
        file.setName(multipartFile.getOriginalFilename());
        String extensao = file.getName().substring(file.getName().lastIndexOf("."));
        file.setUrl(UUID.randomUUID().toString() + extensao);
        saveOnDisc(multipartFile.getBytes(), file.getUrl());
        file.setMimeType(multipartFile.getContentType());
        br.gov.es.participe.model.File pl = fileRepository.save(file);
        return new FileDto(pl);
    }

    private void saveOnDisc(byte [] dados, String nomeArquivo) throws IOException {
        String caminhoArquivo = applicationProperties.getPathImagens().concat(nomeArquivo);
        File file = Files.createFile(Paths.get(caminhoArquivo)).toFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(dados);
        out.flush();
        out.close();
    }

    public void delete(Long id) {
        br.gov.es.participe.model.File file1 = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Imagem id: " + id +" não encontrada."));
        String caminhoArquivo = applicationProperties.getPathImagens().concat(file1.getUrl());
        File file = Paths.get(caminhoArquivo).toFile();
        if (file.exists()) {
            file.delete();
        }
        fileRepository.delete(file1);
    }

}
