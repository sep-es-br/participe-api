/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.service;

import br.gov.es.participe.configuration.RocksDBConfig;
import br.gov.es.participe.controller.dto.PersonListItemsResponse;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.controller.dto.UnitRolesDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcSectionInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class CacheService {
    
    private final RocksDB db;
    private final ColumnFamilyHandle abaPersons;
    
    private final Logger logger;
    private final AcessoCidadaoService acSrv;
    private final ObjectMapper objMapper = new ObjectMapper();
    
    
    private final ConcurrentHashMap<String, List<PersonListItemsResponse>> cacheGuidOrgPerson = new ConcurrentHashMap();
    
    private final ConcurrentHashMap<String, Optional<AcSectionInfoDto>> cacheGuidSection = new ConcurrentHashMap();

    public CacheService(
            RocksDB db, 
            List<ColumnFamilyHandle> handles,
            Logger logger,
            AcessoCidadaoService acSrv
        ) {
        this.db = db;
        this.logger = logger;
        this.acSrv = acSrv;
        
        this.abaPersons = handles.stream()
                .filter(h -> {
                    try { return new String(h.getName()).equals(RocksDBConfig.TAB_CACHE_PERSONS); } 
                    catch (Exception e) { return false; }
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aba '" + RocksDBConfig.TAB_CACHE_PERSONS + "' não inicializada!"));
        
    }
    
    public void salvarPersons(String guidOrg, String jsonConteudo) {
        try {
            db.put(abaPersons, guidOrg.getBytes(), jsonConteudo.getBytes());
        } catch (Exception e) {
            System.err.println("Erro ao salvar no RocksDB: " + e.getMessage());
        }
    }

    // Método pra buscar o JSON do Plot (Onde o O(1) acontece ⚡)
    public String buscarPersons(String guidOrg) {
        try {
            byte[] dados = db.get(abaPersons, guidOrg.getBytes());
            return dados != null ? new String(dados) : null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar no RocksDB: " + e.getMessage());
            return null;
        }
    }
    
    @Async
    @Scheduled(cron = "0 0 4 ? * * *")
    public void loadPersonsCache(List<PublicAgentDto> agentesList) {
        logger.info("Carregando a lista de agentes+papel...");
        long startTime = System.currentTimeMillis();
        List<PublicAgentDto> agentesTodos = Optional.ofNullable(agentesList)
                                                .orElseGet(() -> 
                                                        this.acSrv.findPublicAgentsFromAcessoCidadaoAPI()
                                                );
        
        
        
        for (PublicAgentDto agente : agentesTodos){

            List<UnitRolesDto> papeis = this.acSrv.findPapeisFromAcessoCidadaoAPIByAgentePublicoSub(agente.getSub());

            UnitRolesDto papel = papeis.stream().filter(UnitRolesDto::isPrioritario).findFirst().orElse(null);
            

            if(papel != null && papel.getLotacaoGuid() != null) {
                AcSectionInfoDto section = 
                        this.cacheGuidSection.computeIfAbsent(
                                papel.getLotacaoGuid(), 
                                key -> Optional.ofNullable(this.acSrv.findSectionInfoFromOrganogramaAPI(key)))
                                            .orElse(null);
                        
                if(section != null && section.getGuidOrganizacao() != null){
                    
                    this.cacheGuidOrgPerson.computeIfAbsent(
                        section.getGuidOrganizacao(),
                        k -> new ArrayList<>()
                    ).add(
                        new PersonListItemsResponse(
                            agente.getSub(),
                            agente.getName(),
                            papel.getNome(),
                            section.getNome()
                        )
                    );
                    
                }
            }


        }
        
        logger.info("Lista de agentes+papel carregado em {}", System.currentTimeMillis() - startTime);
        logger.info("salvando em cache...");
        startTime = System.currentTimeMillis();
        this.cacheGuidOrgPerson.forEach((key, value) -> {
            try {
                this.salvarPersons(key, objMapper.writeValueAsString(value));
            } catch( JsonProcessingException ex ) {
                logger.error("Erro ao gerar Json da lista", ex);
            }
        });
        logger.info("salvamento em cache concluido em {}", System.currentTimeMillis() - startTime);
        
        
        
    }
    
}
