package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class IsLinkedByService {

    @Autowired
    private IsLinkedByRepository isLinkedByRepository;

    public IsLinkedBy findByExternaContentUrlAndConferenceId(String url, Long idConference) {
        return isLinkedByRepository.findByExternaContentUrlAndConferenceId(url, idConference);
    }

    public List<IsLinkedBy> findByExternaContentUrlAndConferenceId(Long id) {
        return isLinkedByRepository.findByConferenceId(id);
    }

    public void saveAll(List<IsLinkedBy> linksToSave) {
        isLinkedByRepository.saveAll(linksToSave);
    }

    public void deleteAll(List<IsLinkedBy> linksToRemove) {
        isLinkedByRepository.deleteAll(linksToRemove);
    }

    public void deleteAllByConference(Conference conference) {
        isLinkedByRepository.deleteAllByConference(conference.getId());
    }
}
