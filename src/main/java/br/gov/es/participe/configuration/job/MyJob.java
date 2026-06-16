package br.gov.es.participe.configuration.job;

import br.gov.es.participe.ParticipeApplication;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.CacheService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyJob implements Job {

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private CacheService cacheSrv;
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<PublicAgentDto> publicAgentsData = acessoCidadaoService.findPublicAgentsFromAcessoCidadaoAPI();
        ParticipeApplication.setPublicAgentsData(publicAgentsData);
        System.out.println("Repopulando a lista de agentes públicos");
        
        System.out.println("Verificando se o cache do RocksDB já existe...");
        
        // 🔍 Checa se a pasta 'dados_cache' existe e tem arquivos de dados (.sst)
        if (!cacheJaInicializado()) {
            System.out.println("Cache não encontrado. Iniciando carga inicial (Prepara o café que vai demorar)...");
            this.cacheSrv.loadPersonsCache(publicAgentsData);
        } else {
            System.out.println("Cache detectado no disco! Pulando o carregamento inicial pesado. 🚀");
        }
        
        
        
    }
    
    /**
     * Método auxiliar para checar se já existem arquivos do RocksDB persistidos
     */
    private boolean cacheJaInicializado() {
        try {
            // Pega o caminho relativo onde a pasta 'dados_cache' deve estar
            Path pastaCache = java.nio.file.Paths.get("cache");
            
            if (!Files.exists(pastaCache)) {
                return false;
            }

            // Vasculha a pasta procurando por arquivos que terminem com ".sst" (arquivos de dados do RocksDB)
            try (Stream<Path> stream = Files.list(pastaCache)) {
                return stream.anyMatch(file -> file.toString().endsWith(".sst"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar pasta de cache: " + e.getMessage());
            return false; // Na dúvida, deixa rodar a carga
        }
    }
    

}
