/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class ReportJobManager {
    
    private final Map<Object, CompletableFuture> futureJobMap = new ConcurrentHashMap<>();
    
    private final Logger log = LoggerFactory.getLogger(ReportJobManager.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }   
       
    public <T> CompletableFuture<T> getOrCreateJob(Object key, Supplier<T> supplier) {
        
        return futureJobMap.computeIfAbsent(key, id -> {
            CompletableFuture<T> wrapper = CompletableFuture.supplyAsync(supplier);
            
            wrapper.whenComplete((result, exception) -> {
                if(exception != null) {
                    log.error("Erro no relatório: ", exception);
                    futureJobMap.remove(id);
                }
            });
            
            scheduler.schedule(() -> futureJobMap.remove(id), 10, TimeUnit.MINUTES);
            
            return wrapper;
            
        });
        
        
    }
    
    public void removeJob(Object key) {
        futureJobMap.remove(key);
    }
    
    
}
