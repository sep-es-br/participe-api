package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.PlanItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlanItemService {

    @Autowired
    private PlanItemRepository planItemRepository;

    @Autowired
    private PlanService planService;

    @Autowired
    private FileService fileService;

    @Autowired
    private StructureItemService structureItemService;

    @Autowired
    private LocalityService localityService;

    public List<PlanItem> findAll() {
        List<PlanItem> planItems = new ArrayList<>();

        planItemRepository
                .findAll()
                .iterator()
                .forEachRemaining(planItem -> planItems.add(planItem));

        return planItems;
    }

    public List<PlanItem> search(String query) {
        List<PlanItem> PlanItem = new ArrayList<>();

        planItemRepository
                .search(query)
                .iterator()
                .forEachRemaining(locality -> PlanItem.add(locality));

        return PlanItem;
    }

    @Transactional
    public PlanItem save(PlanItem planItem) {
        loadAttributes(planItem);

        return planItemRepository.save(planItem);
    }

    public PlanItem find(Long id) {
        PlanItem planItem = planItemRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan item not found: " + id));

        return planItem;
    }

    @Transactional
    public void delete(Long id) {
        PlanItem planItem = find(id);

        if (planItem.getFile() != null) {
            fileService.delete(planItem.getFile().getId());
        }

        planItemRepository.delete(planItem);
    }

    private void loadAttributes(PlanItem planItem) {
        loadPlan(planItem);
        loadParent(planItem);
        loadLocalities(planItem);
        loadFile(planItem);
        loadStructureItem(planItem);
    }

    private void loadLocalities(PlanItem planItem) {
        Set<Long> ids = planItem.getLocalitiesIds();
        if (planItem.getId() != null) {
            PlanItem planItemTemp = find(planItem.getId());
            if (planItemTemp.getLocalities() != null && !planItemTemp.getLocalities().isEmpty()) {
                if (ids != null) {
                    planItemTemp.getLocalities().removeIf( locality -> !ids.contains(locality.getId()));
                } else {
                    planItemTemp.getLocalities().clear();
                }
                planItemRepository.save(planItemTemp);
            }
        }
        if (ids != null && !ids.isEmpty()) {
            if(planItem.getLocalities() == null) {
                planItem.setLocalities(new HashSet<>());
            }
            planItem.getLocalitiesIds().forEach(id -> {
                Locality locality = localityService.find(id);
                planItem.getLocalities().add(locality);
            });
        }
    }

    private void loadPlan(PlanItem planItem) {
        if (planItem.getPlan() != null && planItem.getPlan().getId() != null) {
            Plan plan = planService.find(planItem.getPlan().getId());
            if (plan != null) {
                planItem.setPlan(plan);
            }
        }
    }

    private void loadParent(PlanItem planItem) {
        if (planItem.getParent() != null && planItem.getParent().getId() != null) {
            PlanItem parent = find(planItem.getParent().getId());
            if (parent != null) {
                planItem.setParent(parent);
            }
        }
    }

    private void loadFile(PlanItem planItem) {
        if (planItem.getFile() != null && planItem.getFile().getId() != null) {
            File file = fileService.find(planItem.getFile().getId());
            if (file != null) {
                planItem.setFile(file);
            }
        }
    }

    private void loadStructureItem(PlanItem planItem) {
        if (planItem.getStructureItem() != null && planItem.getStructureItem().getId() != null) {
            StructureItem structureItem = structureItemService.find(planItem.getStructureItem().getId());
            if (structureItem != null) {
                planItem.setStructureItem(structureItem);
            }
        }
    }
}