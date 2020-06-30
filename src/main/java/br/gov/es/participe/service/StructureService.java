package br.gov.es.participe.service;

import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.StructureItemRepository;
import br.gov.es.participe.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
public class StructureService {

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private StructureItemRepository structureItemRepository;

    public List<Structure> findAll(String query) {
        List<Structure> structures = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            query = query.replaceAll("[^a-zà-úA-ZÀ-Ú0-9ç]+", " ");
        	query = query.trim().replaceAll(" +", " ");
        	String newQuery = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            structureRepository
                    .findByName(newQuery)
                    .iterator()
                    .forEachRemaining(structures::add);
        } else {
            structureRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(structures::add);
        }

        return structures;
    }

    @Transactional
    public Structure save(Structure structure) {
    	String newName = structure.getName().trim().replaceAll(" +", " ");
    	structure.setName(newName);
        return structureRepository.save(structure);
    }

    public Structure find(Long id) {

        return structureRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Structure structure = structureRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found: " + id));

        if (!structure.getItems().isEmpty()) {
            for (StructureItem structureItem : structure.getItems()) {
                structureItemRepository.delete(structureItem);
            }
        }

        structureRepository.delete(structure);
    }
}
