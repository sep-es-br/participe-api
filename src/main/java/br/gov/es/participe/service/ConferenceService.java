package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.repository.ConferenceRepository;

@Service
public class ConferenceService {

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PlanService planService;

    public boolean validate(String name, Long id) {
        Conference conference = conferenceRepository.findByNameIgnoreCase(name);
        if(conference == null){
            return true;
        }
        return conference.getId().equals(id);
    }

    public List<Conference> findAll(String name, Long plan, Integer month, Integer year) {
        List<Conference> conferences = new ArrayList<>();
        if (year != null) {

        }
        conferenceRepository
            .findAllByQuery(name, plan, month, year)
            .iterator()
            .forEachRemaining(conference -> conferences.add(conference));

        return conferences;
    }

    @Transactional
    public Conference save(Conference conference) {
        if (conference.getPlan() == null || conference.getPlan().getId() == null) {
            throw new IllegalArgumentException("Plan is required");
        }
        Conference c = conferenceRepository.findByNameIgnoreCase(conference.getName());
        if (c != null) {
            if (conference.getId() != null) {
                if (!conference.getId().equals(c.getId())) {
                    throw new IllegalArgumentException("This name already exists");
                }
            } else{
                throw new IllegalArgumentException("This name already exists");
            }
        }
        if (conference.getPlan().getId() != null) {
            if (conference.getId() != null) {
                Conference conference1 = find(conference.getId());
                conference1.setPlan(null);
                conferenceRepository.save(conference1);
            }
            conference.setPlan(planService.find(conference.getPlan().getId()));
        }
        return conferenceRepository.save(conference);
    }

    public Conference find(Long id) {
        Conference conference = conferenceRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + id));

        return conference;
    }

    @Transactional
    public void delete(Long id) {
        Conference conference = find(id);

        conferenceRepository.delete(conference);
    }
}
