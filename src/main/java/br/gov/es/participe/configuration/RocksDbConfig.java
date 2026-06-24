/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author gean.carneiro
 */
@Configuration
public class RocksDbConfig {

    @Bean
    public RocksDB rocksDB() throws RocksDBException {
        // Carrega as bibliotecas nativas do RocksDB
        RocksDB.loadLibrary();
        
        String jarPath;
        try {
            jarPath = System.getProperty("user.dir");

// Diretório onde os dados serão armazenados
Path dbPath = Path.of(jarPath, "rocksdb-store");
;
        } catch(Exception e) {
            jarPath = Paths.get(".").toAbsolutePath().normalize().toString();
        }

        // Diretório onde os dados serão armazenados (ex: no disco)
        Path dbPath = Path.of(jarPath, "rocksdb-store");
        
        try {
            Files.createDirectories(dbPath);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível criar o diretório do RocksDB", e);
        }

        // Configurações do banco (ex: criar DB se não existir)
        try (Options options = new Options().setCreateIfMissing(true)) {
            return RocksDB.open(options, dbPath.toString());
        }
    }
}
