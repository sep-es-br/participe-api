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

        validateStructureItem(structureItem);

        return structureItemRepository.save(structureItem);
    }

    private void validateStructureItem(StructureItem structureItem) {
        if(structureItem.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The name cannot be empty");
        }
    }

    @Transactional
    public StructureItem update(Long id, StructureItemDto structureItemDto) {
        StructureItem structureItem = find(id);

        if(structureItemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("The name cannot be empty");
        }

        structureItem.setName(structureItemDto.getName());
        structureItem.setLogo(structureItemDto.getLogo());
        structureItem.setLocality(structureItemDto.getLocality());
        structureItem.setVotes(structureItemDto.getVotes());
        structureItem.setComments(structureItemDto.getComments());
        structureItem.setTitle(structureItemDto.getTitle());
        structureItem.setSubtitle(structureItemDto.getSubtitle());
        structureItem.setLink(structureItemDto.getLink());
        
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
    
    public StructureItem findByIdPlanItem(Long idPlanItem) {
    	return structureItemRepository.findByIdPlanItem(idPlanItem);
    }
    
    public StructureItem findChild(Long idParent) {
    	return structureItemRepository.findChild(idParent);
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

    public List<StructureItem> findByStructure(Long idStructure) {
        return structureItemRepository.findByStructure(idStructure);
    }

    public List<StructureItem> findByIds(List<Long> targetedByItems) {
        return structureItemRepository.findByIds(targetedByItems);
    }
}
