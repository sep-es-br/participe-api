package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private LocalityTypeService localityTypeService;
    
    @Autowired
    private StructureService structureService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private PlanItemService planItemService;

    @Autowired
    private ConferenceService conferenceService;

    public List<Plan> findAll(String query) {
        List<Plan> plans = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            query = query.replaceAll("[^a-zà-úA-ZÀ-Ú0-9ç]+", " ");
        	query = query.trim().replaceAll(" +", " ");
        	String newQuery = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""); 
            
            planRepository
                    .findByName(newQuery.trim())
                    .iterator()
                    .forEachRemaining(plans::add);
        } else {
            planRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(plans::add);
        }

        return plans;
    }

    public Plan findByConference(Long id) {
    	return planRepository.findByConference(id);
    }
    
    public Plan findByConferenceWithPlanItem(Long id) {
    	return planRepository.findByConferenceWithPlanItem(id);
    }
    
    public Plan findByPlanItem(Long id) {
    	return planRepository.findByPlanItem(id);
    }

    public Collection<Plan> findByDomain(Long id) {
        return planRepository.findByDomain(id);
    }
    
    @Transactional
    public Plan save(Plan plan) {
        if (plan.getId() != null) {
            Plan oldPlan = find(plan.getId());

            if (!oldPlan.getStructure().getId().equals(plan.getStructure().getId())) {
                oldPlan.getStructure().removePlan(plan.getId());
                structureService.save(oldPlan.getStructure());
            }

            if ( oldPlan.getDomain() != null && !oldPlan.getDomain().getId().equals(plan.getDomain().getId())) {
                oldPlan.getDomain().removePlan(plan.getId());
                domainService.save(oldPlan.getDomain());
            }
        }

        loadAttributes(plan);

        return planRepository.save(plan);
    }

    public Plan find(Long id) {

        return planRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + id));
    }
    
    public Plan findFilesById(Long id) {
    	return planRepository.findFilesById(id);
    }

    @Transactional
    public void delete(Long id) {
        Plan plan = find(id);

        List<Conference> conferences = conferenceService.findByPlan(plan.getId());

        if (conferences != null && !conferences.isEmpty()) {
            throw new IllegalArgumentException("This plan is in use by a conference");
        }

        if (!plan.getItems().isEmpty()) {
            for (PlanItem planItem : plan.getItems()) {
                planItemService.delete(planItem.getId());
            }
        }

        planRepository.delete(plan);
    }

    private void loadAttributes(Plan plan) {
        loadStructure(plan);
        loadDomain(plan);
        loadLocalityType(plan);
    }
    

    private void loadStructure(Plan plan) {
        if (plan.getStructure() != null) {
            Structure structure = structureService.find(plan.getStructure().getId());
            if (structure != null) {
                plan.setStructure(structure);
            }
        }
    }
    
    private void loadLocalityType(Plan plan) {
        if (plan.getlocalitytype() != null && plan.getlocalitytype().getId() !=null) {
        	LocalityType type = localityTypeService.find(plan.getlocalitytype().getId());
            if (type != null) {
                plan.setlocalitytype(type);
            }
        }
    }
    

    private void loadDomain(Plan plan) {
        if (plan.getDomain() != null) {
            if (plan.getDomain().getId() != null) {
                Domain domain = domainService.find(plan.getDomain().getId());
                if (domain != null) {
                    plan.setDomain(domain);
                }
            } else {
                plan.setDomain(null);
            }
        }
    }
}
