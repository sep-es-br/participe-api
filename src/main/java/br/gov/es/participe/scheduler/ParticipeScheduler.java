package br.gov.es.participe.scheduler;

import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.ResearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParticipeScheduler {

  private final ConferenceService conferenceService;

  private final ResearchService researchService;

  @Autowired
  public ParticipeScheduler(ConferenceService conferenceService, ResearchService researchService) {
    this.conferenceService = conferenceService;
    this.researchService = researchService;
  }


  @Scheduled(cron = "0 0/1 * * * *")
  public void updateAutomaticConferenceAndResearch() {
    this.researchService.updateAutomaticResearchService();
    this.conferenceService.updateAutomaticConference();
  }

}
