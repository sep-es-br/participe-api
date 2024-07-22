package br.gov.es.participe.configuration.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.es.participe.ParticipeApplication;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.service.AcessoCidadaoService;

public class MyJob implements Job {

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private ParticipeApplication participeApplication;

    private static List<PublicAgentDto> publicAgentsData;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {    
        publicAgentsData = acessoCidadaoService.findPublicAgentsFromAcessoCidadaoAPI();
        participeApplication.setPublicAgentsData(publicAgentsData);
    }

}
