/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.configuration;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author gean.carneiro
 */
@Configuration
public class RocksDBConfig {
    
    public static final String TAB_CACHE_PERSONS = "cache_persons";
    
    @Bean
    public RocksDB rocksDB(List<ColumnFamilyHandle> columnFamilyHandles) throws Exception {
        // Carrega os binários nativos do RocksDB
        RocksDB.loadLibrary();

        // Pega o caminho de onde o JAR está rodando pra salvar a pasta do lado dele
        String caminhoDoJar = RocksDBConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String caminhoDecodificado = URLDecoder.decode(caminhoDoJar, StandardCharsets.UTF_8);
        File arquivoJar = new File(caminhoDecodificado);
        File pastaDoJar = arquivoJar.getParentFile();
        
        // Define a pasta do cache
        File pastaCache = new File(pastaDoJar, "cache");
        if (!pastaCache.exists()) {
            pastaCache.mkdirs();
        }

        // Configurações para abrir o banco e criar as abas se sumirem
        DBOptions dbOptions = new DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
        ColumnFamilyOptions cfOptions = new ColumnFamilyOptions();

        List<ColumnFamilyDescriptor> cfDescriptors = List.of(
            new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOptions),
            new ColumnFamilyDescriptor(TAB_CACHE_PERSONS.getBytes(), cfOptions)
        );

        // Abre o banco. A lista 'columnFamilyHandles' será preenchida pelo RocksDB aqui dentro!
        return RocksDB.open(dbOptions, pastaCache.getAbsolutePath(), cfDescriptors, columnFamilyHandles);
    }

    @Bean
    public List<ColumnFamilyHandle> columnFamilyHandles() {
        return new ArrayList<>();
    }
}
