package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.LeanPlanItemResultDto;
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
	
	private static final String PLAN_ITEM_NOT_FOUND = "Plan item not found: ";

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
                .forEachRemaining(planItems::add);

        return planItems;
    }

    public LeanPlanItemResultDto findPlanItemsByConference(Long id) {
        return planItemRepository.findByConferenceIdWithDto(id);
    }

    public List<PlanItem> findByIdConference(Long id) {
        return planItemRepository.findByIdConference(id);
    }

    public List<PlanItem> search(String query) {
        List<PlanItem> planItems = new ArrayList<>();

        planItemRepository
                .search(query)
                .iterator()
                .forEachRemaining(planItems::add);

        return planItems;
    }


    public PlanItem save(PlanItem planItem) {
        loadAttributes(planItem);

        return planItemRepository.save(planItem);
    }

    public PlanItem find(Long id) {
        return planItemRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PLAN_ITEM_NOT_FOUND + id));
    }
    
    public PlanItem findByIdWithLocalities(Long id) {
        return planItemRepository
                .findByIdWithLocalities(id)
                .orElseThrow(() -> new IllegalArgumentException(PLAN_ITEM_NOT_FOUND + id));
    }

    public PlanItem findByPlanItemChildren(Long idPlanItem, Long idLocality){
        return planItemRepository.findByPlanItemChildren(idPlanItem,idLocality).orElseThrow(() -> new IllegalArgumentException(PLAN_ITEM_NOT_FOUND + idPlanItem));
    }
    
    public List<PlanItem> findChildren(Long idPlanItem){
    	return planItemRepository.findChildren(idPlanItem);
    }

    public PlanItem findFatherPlanItem(Long id) {
        return planItemRepository
                .findFatherPlanItem(id)
                .orElseThrow(() -> new IllegalArgumentException(PLAN_ITEM_NOT_FOUND + id));
    }

    public List<PlanItem> findAllByIdPlan(Long idPlan){
    	return planItemRepository.findAllByIdPlan(idPlan);
    }
    
    public PlanItem findParentsByCommentId(Long idComment) {
    	return planItemRepository.findParentsByCommentId(idComment);
    }
    
   
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
        loadChildren(planItem);
        loadLocalities(planItem);
        loadFile(planItem);
        loadStructureItem(planItem);
        loadAttend(planItem);
    }

    @Transactional
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

    private void loadChildren(PlanItem planItem) {
        if (planItem.getId() != null) {
            PlanItem tmpPlanItem = find(planItem.getId());

            planItem.setChildren(tmpPlanItem.getChildren());
        }
    }
    
    private void loadAttend(PlanItem planItem) {
    	 if (planItem.getId() != null) {
             PlanItem tmpPlanItem = find(planItem.getId());

             planItem.setAttends(tmpPlanItem.getAttends());
         }
    }
}
