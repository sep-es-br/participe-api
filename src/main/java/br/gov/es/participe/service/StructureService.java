package br.gov.es.participe.service;

import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.StructureItemRepository;
import br.gov.es.participe.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            structureRepository
                    .findByName(query.trim())
                    .iterator()
                    .forEachRemaining(domain -> structures.add(domain));
        } else {
            structureRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(domain -> structures.add(domain));
        }

        return structures;
    }

    @Transactional
    public Structure save(Structure structure) {
        return structureRepository.save(structure);
    }

    public Structure find(Long id) {
        Structure structure = structureRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found: " + id));

        return structure;
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
