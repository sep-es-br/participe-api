package br.gov.es.participe.service;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.repository.LocalityRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LocalityService {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LocalityTypeService localityTypeService;

    @Autowired
    private DomainService domainService;

    public List<Locality> findAll() {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .findAll()
                .iterator()
                .forEachRemaining(locality -> localities.add(locality));

        return localities;
    }

    public List<Locality> search(String query) {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .search(query)
                .iterator()
                .forEachRemaining(locality -> localities.add(locality));

        return localities;
    }

    @Transactional
    public Locality create(Locality locality) {
        Locality existentLocality = find(locality.getName());

        if (existentLocality != null) {
            Set<Domain> domains = new HashSet<>();
            for (Domain localityDomain : locality.getDomains()) {
                domains.add(domainService.find(localityDomain.getId()));
            }
            existentLocality.addDomains(domains);
            locality = existentLocality;
        } else {
            loadAttributes(locality);
        }

        return localityRepository.save(locality);
    }

    @Transactional
    public Locality update(Long id, String name) {
        Locality locality = find(id);

        locality.setName(name);

        return localityRepository.save(locality);
    }

    public Locality find(Long id) {
        Locality locality = localityRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locality not found: " + id));

        return locality;
    }

    public List<Locality> findByDomain(Long idDomain) {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .findByDomain(idDomain)
                .iterator()
                .forEachRemaining(locality -> localities.add(locality));

        return localities;
    }

    public Locality find(String name) {
        Locality locality = localityRepository
                .findByNameIgnoreCase(name);

        return locality;
    }

    @Transactional
    public void delete(Long idLocality, Long idDomain) {
        Locality locality = find(idLocality);
        Domain domain = domainService.find(idDomain);

        if (locality.getChildren() != null && locality.getChildren().size() > 0) {
            List<Locality> childrenInDomain = localityRepository.findChildren(idDomain, locality.getId());

            if (childrenInDomain != null && !childrenInDomain.isEmpty()) {
                for (Locality child : childrenInDomain) {
                    delete(child.getId(), idDomain);
                }
            }
        }

        if (locality.getDomains() != null && locality.getDomains().size() == 1) {
            localityRepository.delete(locality);
            return;
        }

        locality.removeDomain(idDomain);
        domain.removeLocality(idLocality);
        localityRepository.save(locality);
    }

    private void loadAttributes(Locality locality) {
        loadType(locality);
        loadDomain(locality);
        loadParents(locality);
    }

    private void loadType(Locality locality) {
        if (locality.getType() != null) {
            locality.setType(localityTypeService.find(locality.getType().getId()));
        }
    }

    private void loadDomain(Locality locality) {
        if (locality.getDomains() != null && !locality.getDomains().isEmpty()) {
            for (Domain domain : locality.getDomains()) {
                Domain parentDomain = domainService.find(domain.getId());
                if (parentDomain != null) {
                    locality.removeDomain(domain.getId());
                    locality.addDomain(parentDomain);
                }
            }
        }
    }

    private void loadParents(Locality locality) {
        if (locality.getParents() != null) {
            for (Locality parent : locality.getParents()) {
                locality.addParent(find(parent.getId()));
            }
        }
    }
}
