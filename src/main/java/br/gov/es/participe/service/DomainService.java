package br.gov.es.participe.service;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.repository.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private LocalityService localityService;

    public List<Domain> findAll(String query) {
        List<Domain> domains = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            domainRepository
                    .findByNameContainingIgnoreCase(query.trim())
                    .iterator()
                    .forEachRemaining(domain -> domains.add(domain));
        } else {
            domainRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(domain -> domains.add(domain));
        }

        return domains;
    }

    @Transactional
    public Domain save(Domain domain) {
        return domainRepository.save(domain);
    }

    public Domain find(Long id) {
        Domain domain = domainRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + id));

        return domain;
    }

    @Transactional
    public void delete(Long id) {
        Domain domain = find(id);

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
}
