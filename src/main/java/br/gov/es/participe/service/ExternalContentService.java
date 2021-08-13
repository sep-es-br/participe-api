package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ExternalContentService {

    @Autowired
    private ExternalContentRepository externalContentRepository;

    public ExternalContent findByUrl(String url) {
        return externalContentRepository.findByUrl(url != null ? url.trim() : null);
    }

    public List<ExternalContent> findExternalContentsByUrls(List<String> urls) {
        return externalContentRepository.findExternalContentsByUrls(urls);
    }

    public void saveAll(List<ExternalContent> externalContentsToSave) {
        externalContentRepository.saveAll(externalContentsToSave);
    }
}
