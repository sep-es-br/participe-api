/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.service;

import java.nio.charset.StandardCharsets;
import javax.annotation.PreDestroy;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class CacheService {
    
    private final RocksDB rocksDB;
    
    public CacheService (RocksDB rocksDB){
        this.rocksDB = rocksDB;
    }
    
    public void salvar(String chave, String valor) throws RocksDBException {
        rocksDB.put(chave.getBytes(StandardCharsets.UTF_8), valor.getBytes(StandardCharsets.UTF_8));
    }
    
    public String buscar(String chave) throws RocksDBException {
        byte[] bytes = rocksDB.get(chave.getBytes(StandardCharsets.UTF_8));
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }
    
    public void deletar(String chave) throws RocksDBException {
        rocksDB.delete(chave.getBytes(StandardCharsets.UTF_8));
    }
    
    @PreDestroy
    public void fechaConexao() {
        if(rocksDB != null) {
            rocksDB.close();
        }
    }
    
}
