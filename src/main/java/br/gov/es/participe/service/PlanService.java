package br.gov.es.participe.service;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private StructureService structureService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private PlanItemService planItemService;

    public List<Plan> findAll(String query) {
        List<Plan> plans = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            planRepository
                    .findByName(query.trim())
                    .iterator()
                    .forEachRemaining(plan -> plans.add(plan));
        } else {
            planRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(domain -> plans.add(domain));
        }

        return plans;
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
        Plan plan = planRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + id));

        return plan;
    }

    @Transactional
    public void delete(Long id) {
        Plan plan = find(id);

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
    }

    private void loadStructure(Plan plan) {
        if (plan.getStructure() != null) {
            Structure structure = structureService.find(plan.getStructure().getId());
            if (structure != null) {
                plan.setStructure(structure);
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
