package br.gov.es.participe.service;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.repository.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private PlanService planService;

    private static final String DOMAIN_ERROR_NOT_FOUND = "domain.error.not-found";

    public List<Domain> findAll(String query) {
        List<Domain> domains = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            domainRepository
                    .findByName(query.trim())
                    .iterator()
                    .forEachRemaining(domains::add);
        } else {
            domainRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(domains::add);
        }

        return domains;
    }

    //@Transactional
    public Domain save(Domain domain) {
        if(domain.getName() == null) {
            throw new IllegalArgumentException("Domain name is required");
        }
        domain.setName(domain.getName().trim().replaceAll("\\s+"," "));
        return domainRepository.save(domain);
    }

    public Domain find(Long id) {

        return domainRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(DOMAIN_ERROR_NOT_FOUND));
    }

    public Domain findWithLocalities(Long id) {

        return Optional.ofNullable(domainRepository
                .findByIdWithLocalities(id))
                       .orElseThrow(() -> new IllegalArgumentException(DOMAIN_ERROR_NOT_FOUND));
    }

    @Transactional
    public void delete(Long id) {
        Domain domain = find(id);

        Collection<Plan> plans = planService.findByDomain(domain.getId());

        if (plans != null && !plans.isEmpty()) {
            throw new IllegalArgumentException("This domain is in use by a plan");
        }

        if (!domain.getLocalities().isEmpty()) {
            List<Locality> localities = localityService.findByDomain(id);

            localities = localities
                    .stream()
                    .filter(locality -> locality.getParents() == null || locality.getParents().isEmpty())
                    .collect(Collectors.toList());

            for (Locality locality : localities) {
                localityService.delete(locality.getId(), id);
            }
        }

        domainRepository.delete(domain);
    }

    @Transactional
    public void deleteAll() {
        domainRepository.deleteAll();
    }
}
