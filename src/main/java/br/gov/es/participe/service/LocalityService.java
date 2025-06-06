package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.LocalityCitizenSelectDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.util.ParticipeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocalityService {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LocalityTypeService localityTypeService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private ParticipeUtils participeUtils;

    private static final Logger log = LoggerFactory.getLogger(LocalityService.class);
    private static final String DOMAIN_ERROR_NOT_FOUND = "domain.error.not-found";

    public List<Locality> findAll() {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .findAll()
                .iterator()
                .forEachRemaining(localities::add);

        return localities;
    }

    public List<Locality> findByIdConference(Long id) {
        List<Locality> localities = new ArrayList<>();
        localityRepository.findCardsByIdConference(id)
                .stream()
                .filter(l -> l.getParents().isEmpty())
                .iterator()
                .forEachRemaining(localities::add);
        if(!localities.isEmpty()) {
          localities.sort((l1, l2) -> l1.getName().trim().compareTo(l2.getName().trim()));
        }
        return localities;
    }
    

    public List<Locality> findByIdConference(Long id, Long localizationId) {
        List<Locality> localities = new ArrayList<>();
        localityRepository.findCardsByIdConference(id)
                .stream()
                .filter(l -> l.getParents().isEmpty())
                .iterator()
                .forEachRemaining(localities::add);
        if(!localities.isEmpty()) {
          localities.sort((l1, l2) -> l1.getName().trim().compareTo(l2.getName().trim()));
        }
        return localities;
    }

    public List<Locality> search(String query, Long type) {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .search(query, type)
                .iterator()
                .forEachRemaining(localities::add);

        return localities;
    }


    public Locality create(Locality locality) {

        if (locality.getName()== null ||locality.getLatitudeLongitude()== null ) {
            throw new IllegalArgumentException("Create:Locality object is null");
        }

        if (isInvalidTypeLevel(locality)) {
            throw new IllegalArgumentException("domain.error.locality.invalid-type-level");
        }

        Locality existentLocality = find(locality.getName(), locality.getType().getId());
        log.info(
          "Locality encontrada utilizando os parâmetros localityName={} e localityId={}",
          locality.getName(),
          locality.getType().getId()
        );
        if (existentLocality != null && locality.getType() != null && locality.getType().getId() != null
                && locality.getType().getId().equals(existentLocality.getType().getId())) {
            Set<Domain> domains = new HashSet<>();
            for (Domain localityDomain : locality.getDomains()) {
                log.info(
                  "Relacionando domainId={} a localityId={}",
                  localityDomain.getId(),
                  locality.getId()
                );
                domains.add(domainService.find(localityDomain.getId()));
            }
            existentLocality.addDomains(domains);

            if (!locality.getParents().isEmpty()) {
                for (Locality localityParent : locality.getParents()) {
                    log.info(
                      "Relacionando localityId={} ao localityParentId={}",
                      locality.getId(),
                      localityParent.getId()
                    );
                    existentLocality.addParent(localityParent);
                }
            }
            locality = existentLocality;
        } else {
            loadAttributes(locality);
        }
        if (locality.getName() != null && !locality.getName().isEmpty()) {
            locality.setName(locality.getName().trim().replaceAll("\\s+", " "));
        }

      final var localityCreated = localityRepository.save(locality);
      log.info(
        "Locality criada/atualizada com localityId={} e localityName={}",
        localityCreated.getId(),
        localityCreated.getName()
      );
      return localityCreated;
    }


    public Locality update(Long id, LocalityParamDto dto) {

        if (dto.getName()== null ||dto.getLatitudeLongitude()== null ) {
            throw new IllegalArgumentException("Update: Locality object is null");
        }

        Locality locality = find(id);
        String name = dto.getName();

        if (name != null && !name.isEmpty()) {
            name = name.trim().replaceAll("\\s+", " ");
        }
        log.info(
          "Alterando name de {} para {} e latitudeLongitude de {} para {} da localityId={}",
          locality.getName(),
          name,
          locality.getLatitudeLongitude(),
          dto.getLatitudeLongitude(),
          locality.getId()
        );
        locality.setName(name);
        locality.setLatitudeLongitude(dto.getLatitudeLongitude());

      final var updatedLocality = localityRepository.save(locality);
      log.info("Dados de localityId={} atualizados com sucesso", locality.getId());
      return updatedLocality;
    }

    public Locality find(Long id) {

        return localityRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Locality not found: " + id));
    }

    public List<Locality> findLocalitiesToComplement(Long idConference, boolean orderByName) {
        if (orderByName) {
            List<Locality> localities = localityRepository.findLocalitiesToComplement(idConference);
            localities.sort(Comparator.comparing(locality -> participeUtils.normalize(locality.getName())));
            return localities;
        } else {
            return localityRepository.findLocalitiesToComplement(idConference);
        }
    }

    public List<Locality> findByDomain(Long idDomain) {
        List<Locality> localities = new ArrayList<>();

        localityRepository
                .findByDomain(idDomain)
                .iterator()
                .forEachRemaining(localities::add);

        return localities;
    }

    public Integer countLocalitiesParticipation(Long idConference) {
        return localityRepository.countLocalitiesParticipation(idConference);
    }

    private boolean isInvalidTypeLevel(Locality locality) {
        Domain domain = getDomainFromLocality(locality);

        if (domain.getLocalities().isEmpty()) {
            return false;
        }

        if (isTypeUsedInAnotherLevel(domain, locality)) {
            return true;
        }

        return isLevelTypeAlreadyBinded(domain, locality);
    }

    private Domain getDomainFromLocality(Locality locality) {
        Domain domain = locality
                .getDomains()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(DOMAIN_ERROR_NOT_FOUND));

        return domainService.findWithLocalities(domain.getId());
    }

    private boolean isTypeUsedInAnotherLevel(Domain domain, Locality locality) {
        int typeLevel = getTypeLevel(domain, domain.getRootLocality(), locality.getType(), 0);
        int localityLevel = getLocalityLevel(domain, locality, 0);

        if (typeLevel != -1 && localityLevel != -1) {
            return typeLevel != localityLevel;
        }

        return false;
    }

    private boolean isLevelTypeAlreadyBinded(Domain domain, Locality locality) {
        LocalityType levelType = getLevelType(domain, locality);

        if (levelType != null) {
            return !levelType.getId().equals(locality.getType().getId());
        }

        return false;
    }

    private LocalityType getLevelType(Domain domain, Locality locality) {
        int localityLevel = getLocalityLevel(domain, locality, 0);

        if (!domain.getLocalities().isEmpty()) {
            Locality sameLevelLocality = domain
                    .getLocalities()
                    .stream()
                    .filter(l -> !l.getId().equals(locality.getId()))
                    .filter(l -> getLocalityLevel(domain, l, 0) == localityLevel)
                    .findFirst()
                    .orElse(null);
            if (sameLevelLocality != null) {
                return sameLevelLocality.getType();
            }
        }

        return null;
    }

    private int getTypeLevel(Domain domain, Locality node, LocalityType type, int level) {
        if (node.getType().getId().equals(type.getId())) {
            return level;
        }

        if (node.getChildren().isEmpty()) {
            return -1;
        }

        Locality firstChild = node
                .getChildren()
                .stream()
                .filter(l -> l.getDomains().contains(domain))
                .findFirst()
                .orElse(null);

        if (firstChild == null) {
            return -1;
        }

        return getTypeLevel(domain, firstChild, type, ++level);
    }

    private int getLocalityLevel(Domain domain, Locality node, int level) {
        if (node.getParents().isEmpty()) {
            return level;
        }

        Locality parent = getParent(domain, node);
        return getLocalityLevel(domain, parent, ++level);
    }

    private Locality getParent(Domain domain, Locality locality) {
        if (locality.getParents().isEmpty()) {
            return null;
        }

        List<Locality> domainLocalities = domain
                .getLocalities()
                .stream()
                .filter(l -> l.getDomains().contains(domain)).collect(Collectors.toList());

        if (locality.getId() == null) {
            Long parentId = locality
                    .getParents()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(DOMAIN_ERROR_NOT_FOUND))
                    .getId();

            return findParentById(domainLocalities, parentId);
        }

        return locality
                .getParents()
                .stream()
                .filter(l -> l.getDomains().contains(domain))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(DOMAIN_ERROR_NOT_FOUND));
    }

    private Locality findParentById(List<Locality> localities, Long id) {
        return localities
                .stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Locality find(String name, Long typeId) {
        List<Locality> localities = localityRepository
                .findByNameAndType(name, typeId);
        if (localities != null && !localities.isEmpty()) {
            return localities.get(0);
        }
        return null;
    }


    public void delete(Long idLocality, Long idDomain) {
        log.info("Iniciando remoção do localityId={} e domainId={}", idLocality, idDomain);
        Locality locality = find(idLocality);
        Domain domain = domainService.find(idDomain);
        log.info("Encontrado localityId={} e domainId={}", idLocality, idDomain);
        if (locality.getChildren() != null && !locality.getChildren().isEmpty()) {
            List<Locality> childrenInDomain = localityRepository.findChildren(idDomain, locality.getId());
            log.info(
              "Foram encontrados {} filhos da localityId={} relacionados ao domainId={}",
              Optional.ofNullable(childrenInDomain).map(List::size).orElse(0),
              locality.getId(),
              idDomain
            );
            if (childrenInDomain != null && !childrenInDomain.isEmpty()) {
                for (Locality child : childrenInDomain) {
                    delete(child.getId(), idDomain);
                }
            }
        }

        if (locality.getDomains() != null && locality.getDomains().size() == 1) {
            log.info(
              "Locality com localityId={} possui apenas um domainId={} relacionado, apagando localityId={}...",
              locality.getId(),
              idDomain,
              locality.getId()
            );
            localityRepository.delete(locality);
            return;
        }

        locality.removeDomain(idDomain);
        domain.removeLocality(idLocality);

        log.info("Removendo relacionamento entre localityId={} e domainId={}", idLocality, idDomain);
        localityRepository.save(locality);
        log.info("Relacionamento entre localityId={} e domainId={} removido com sucesso", idLocality, idDomain);
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

    public LocalityCitizenSelectDto getLocalitiesToDisplay(Long conferenceId, String name) {
        if(conferenceId == null) {
            throw new IllegalArgumentException("Conference Id must be informed.");
        }
        return localityRepository.getLocalitiesToDisplay(conferenceId, name);
    }
}
