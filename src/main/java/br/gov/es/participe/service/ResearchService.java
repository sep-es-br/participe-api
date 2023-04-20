package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.util.*;
import br.gov.es.participe.util.domain.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.*;

@Service
public class ResearchService {
    static Logger log = Logger.getLogger(ResearchService.class.getName());

    @Autowired
    private ResearchRepository researchRepository;

    @Autowired
    private ParticipeUtils participeUtils;

    public Optional<Research> findByIdConference(Long idConference) {
        return Optional.ofNullable(researchRepository.findByConferenceId(idConference));
    }

    public void save(Research research) {
        researchRepository.save(research);
    }

    @Transactional
    public void updateAutomaticResearchService() {
        List<Research> researches = researchRepository.findAllAutomatic();
        if (researches != null) {
            researches.forEach(this::checkResearchStatus);
            researchRepository.saveAll(researches);
        }
    }

    private void checkResearchStatus(Research research) {
        try {
            research.setStatusType(ResearchDisplayStatusType.INACTIVE);

            if ((research.getBeginDate() != null || research.getEndDate() != null) && participeUtils.isActive(research.getBeginDate(), research.getEndDate())) {
                research.setStatusType(ResearchDisplayStatusType.ACTIVE);
            }
        } catch (Exception e) {
            log.throwing(Conference.class.getName(), "updateAutomaticConference", e);
        }
    }
}
