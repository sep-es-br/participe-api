package br.gov.es.participe.configuration.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.es.participe.ParticipeApplication;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.service.AcessoCidadaoService;
import org.springframework.stereotype.Component;

@Component
public class MyJob implements Job {

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<PublicAgentDto> publicAgentsData = acessoCidadaoService.findPublicAgentsFromAcessoCidadaoAPI();
        ParticipeApplication.setPublicAgentsData(publicAgentsData);
        System.out.println("Repopulando a lista de agentes públicos");
    }

}
