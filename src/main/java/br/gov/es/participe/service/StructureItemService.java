package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.StructureItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StructureItemService {

    @Autowired
    private StructureItemRepository structureItemRepository;

    @Autowired
    private StructureService structureService;

    public List<StructureItem> findAll() {
        List<StructureItem> structureItems = new ArrayList<>();

        structureItemRepository
                .findAll()
                .iterator()
                .forEachRemaining(structureItems::add);

        return structureItems;
    }

    public List<StructureItem> search(String query) {
        List<StructureItem> structureItems = new ArrayList<>();

        structureItemRepository
                .search(query)
                .iterator()
                .forEachRemaining(structureItems::add);

        return structureItems;
    }

    @Transactional
    public StructureItem create(StructureItem structureItem) {
        loadAttributes(structureItem);

        return structureItemRepository.save(structureItem);
    }

    @Transactional
    public StructureItem update(Long id, StructureItemDto structureItemDto) {
        StructureItem structureItem = find(id);

        structureItem.setName(structureItemDto.getName());
        structureItem.setLogo(structureItemDto.getLogo());
        structureItem.setLocality(structureItemDto.getLocality());
        structureItem.setVotes(structureItemDto.getVotes());
        structureItem.setComments(structureItemDto.getComments());

        return structureItemRepository.save(structureItem);
    }

    public StructureItem find(Long id) {

        return structureItemRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Structure item not found: " + id));
    }

    public StructureItem find(String name) {

        return structureItemRepository
                .findByNameIgnoreCase(name);
    }

    @Transactional
    public void delete(Long id) {
        StructureItem structureItem = find(id);

        if (structureItem.getChildren() != null && !structureItem.getChildren().isEmpty()) {
            for (StructureItem child : structureItem.getChildren()) {
                delete(child.getId());
            }
        }

        structureItemRepository.delete(structureItem);
    }

    private void loadAttributes(StructureItem structureItem) {
        if (structureItem.getParent() != null) {
            loadParent(structureItem);
        } else {
            loadStructure(structureItem);
        }
    }

    private void loadParent(StructureItem structureItem) {
        if (structureItem.getParent() != null) {
            StructureItem parent = find(structureItem.getParent().getId());
            if (parent != null) {
                structureItem.setParent(parent);
                structureItem.setStructure(null);
            }
        }
    }

    private void loadStructure(StructureItem structureItem) {
        if (structureItem.getStructure() != null) {
            Structure parentStructure = structureService.find(structureItem.getStructure().getId());
            if (parentStructure != null) {
                structureItem.setStructure(parentStructure);
            }
        }
    }
}
