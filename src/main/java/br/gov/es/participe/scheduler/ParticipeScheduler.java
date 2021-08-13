package br.gov.es.participe.scheduler;

import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.ResearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParticipeScheduler {

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private ResearchService researchService;

    @Scheduled(cron = "0 0/5 * * * *")
    public void updateAutomaticConferenceAndResearch() {
        researchService.updateAutomaticResearchService();
        conferenceService.updateAutomaticConference();
    }

}
