package br.gov.es.participe.service;

//import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;


    public List<Topic> findAllByConference(Long idConference) {
        return topicRepository.findAllByConference(idConference);
    }

    public void saveAll(List<Topic> topicsToSave) {
        topicRepository.saveAll(topicsToSave);
    }

    public void deleteAll(List<Topic> listToRemove) {
        topicRepository.deleteAll(listToRemove);
    }

    public void deleteAllByConference(Conference conference) {
        topicRepository.deleteAllByConference(conference.getId());
    }
}
