/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author gean.carneiro
 */
@Configuration
public class RocksDbConfig {
    
    @Value("${app.cachePath}")
    private String cachePath;

    @Bean
    public RocksDB rocksDB() throws RocksDBException {
        // Carrega as bibliotecas nativas do RocksDB
        RocksDB.loadLibrary();

        // 1. Pega o diretório atual de execução do sistema de forma segura

        // 2. Cria o caminho da pasta onde os dados serão salvos
        Path dbPath = Path.of(cachePath, "rocksdb-store");

        // 3. Tenta criar o diretório fisicamente se ele não existir
        try {
            Files.createDirectories(dbPath);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível criar o diretório do RocksDB", e);
        }

        // 4. Configurações do banco e inicialização
        try (Options options = new Options().setCreateIfMissing(true)) {
            return RocksDB.open(options, dbPath.toString());
        }
    }

}
