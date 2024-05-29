package br.gov.es.participe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesRequestDto;
import br.gov.es.participe.exception.ParticipeServiceException;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;
import br.gov.es.participe.repository.EvaluatorsRepository;

@Service
public class EvaluatorsService {
    
    @Autowired
    private EvaluatorsRepository evaluatorsRepository;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    private static final Logger log = LoggerFactory.getLogger(EvaluatorsService.class);

    public Page<EvaluatorResponseDto> findAllEvaluators(Pageable pageable) {

        Page<EvaluatorResponseDto> evaluatorsList = evaluatorsRepository.findAllEvaluators(pageable);

        return evaluatorsList;

    }

    public EvaluatorResponseDto saveEvaluator(EvaluatorRequestDto evaluatorRequestDto) {

        Organization evaluatorOrganization = this.persistOrganization(evaluatorRequestDto.getOrganizationGuid());

        Set<Section> evaluatorSections = this.persistSections(evaluatorRequestDto.getSectionsGuid(), evaluatorOrganization);

        Set<Role> evaluatorRoles = this.persistRoles(evaluatorRequestDto.getRolesGuid(), evaluatorSections);

        return new EvaluatorResponseDto(evaluatorOrganization, evaluatorSections, evaluatorRoles);

    }

    public EvaluatorResponseDto updateEvaluator(EvaluatorRequestDto evaluatorRequestDto, Long evaluatorId) {

        Organization evaluatorOrganization = evaluatorsRepository.findOrganizationById(evaluatorId)
            .orElseThrow(() -> new ParticipeServiceException("Organização não encontrada com o id fornecido"));

        Set<Section> evaluatorSections = this.updateSections(evaluatorRequestDto.getSectionsGuid(), evaluatorOrganization);

        Set<Role> evaluatorRoles = this.updateRoles(evaluatorRequestDto.getRolesGuid(), evaluatorSections);

        return new EvaluatorResponseDto(evaluatorOrganization, evaluatorSections, evaluatorRoles);

    }

    private Organization persistOrganization(String orgGuid) {

        log.info("Persistindo organizacao com guid={}", orgGuid);
        Optional<Organization> organization = evaluatorsRepository.findOrganizationByGuid(orgGuid);

        if(organization.isPresent()){
            throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid desta organização.");
        } else {
            Organization newOrganization = new Organization(orgGuid);
            evaluatorsRepository.save(newOrganization);
            return newOrganization;
        }

    }
    
    private Set<Section> persistSections(List<String> sectionsGuid, Organization organization) {

        Set<Section> evaluatorSectionsSet = new HashSet<Section>();

        sectionsGuid.iterator().forEachRemaining((guid) -> {
            log.info("Persistindo setor com guid={}", guid);
            Optional<Section> section = evaluatorsRepository.findSectionByGuid(guid);

            if(section.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste setor.");
            } else {
                Section newSection = new Section(guid);
                log.info("Criando relacionamento entre setor={} e organizacao={}", guid, organization.getGuid());
                newSection.setOrganization(organization);
                evaluatorsRepository.save(newSection);
                evaluatorSectionsSet.add(newSection);
            }
        });

        return evaluatorSectionsSet;

    }

    private Set<Section> updateSections(List<String> sectionsGuid, Organization organization) {

        log.info("Buscando setores pertencentes a organizacao={}", organization.getId().toString());
        List<Section> evaluatorSectionsList = evaluatorsRepository.findAllSectionsWithRelationshipToOrganization(organization.getId());

        evaluatorSectionsList.iterator().forEachRemaining((section) -> evaluatorsRepository.deleteById(section.getId()));

        Set<Section> evaluatorSectionsSet = new HashSet<Section>();

        sectionsGuid.iterator().forEachRemaining((guid) -> {
            log.info("Buscando setor com guid={}", guid);
            Section candidateSection = evaluatorsRepository.findSectionByGuid(guid)
                .orElse(new Section(guid));
            
            log.info("Criando relacionamento entre setor={} e organizacao={}", guid, organization.getId().toString());
            candidateSection.setOrganization(organization);
            evaluatorsRepository.save(candidateSection);
            evaluatorSectionsSet.add(candidateSection);
        });

        return evaluatorSectionsSet;

    }
    
    private Set<Role> persistRoles(List<String> rolesGuid, Set<Section> evaluatorSections) {

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(rolesGuid.contains("all")){
            log.info("Nenhum guid foi fornecido, nenhum papel sera persistido no banco");
            evaluatorRolesSet.add(new Role("all"));
            return evaluatorRolesSet;
        }

        rolesGuid.iterator().forEachRemaining((guid_lotacao) -> {

            String guid = guid_lotacao.split(":")[0];
            String lotacao = guid_lotacao.split(":")[1];

            log.info("Persistindo papel com guid={}", guid);
            Optional<Role> role = evaluatorsRepository.findRoleByGuid(guid);

            if(role.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste papel.");
            } else {
                Role newRole = new Role(guid);
                log.info("Buscando no banco setor com guid={}", lotacao);
                Section targetSection = new ArrayList<Section>(evaluatorSections).stream().filter(
                    (section) -> section.getGuid().equals(lotacao))
                    .findFirst()
                    .get();
                log.info("Criando relacionamento entre papel={} e setor={}", guid, targetSection.getGuid());
                newRole.setSection(targetSection);
                evaluatorsRepository.save(newRole);
                evaluatorRolesSet.add(newRole);
            }
        });

        return evaluatorRolesSet;

    }

    private Set<Role> updateRoles(List<String> rolesGuid, Set<Section> evaluatorSections) {

        evaluatorSections.iterator().forEachRemaining((section) -> {
            log.info("Buscando papeis pertencentes ao setor={}", section.getId().toString());
            List<Role> evaluatorRolesList = evaluatorsRepository.findAllRolesWithRelationshipToSection(section.getId());
            evaluatorRolesList.iterator().forEachRemaining((role) -> evaluatorsRepository.deleteById(role.getId()));
        });

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(rolesGuid.contains("all")){
            log.info("Nenhum guid foi fornecido, nenhum papel sera persistido no banco");
            evaluatorRolesSet.add(new Role("all"));
            return evaluatorRolesSet;
        }

        rolesGuid.iterator().forEachRemaining((guid_lotacao) -> {
            String guid = guid_lotacao.split(":")[0];
            String lotacao = guid_lotacao.split(":")[1];

            log.info("Buscando papel com guid={}", guid);
            Role candidateRole = evaluatorsRepository.findRoleByGuid(guid)
                .orElse(new Role(guid));

            log.info("Buscando no banco setor com guid={}", lotacao);
            Section targetSection = new ArrayList<Section>(evaluatorSections).stream().filter(
                (section) -> section.getGuid().equals(lotacao))
                .findFirst()
                .get();
            
            log.info("Criando relacionamento entre papel={} e setor={}", guid, targetSection.getGuid());
            candidateRole.setSection(targetSection);
            evaluatorsRepository.save(candidateRole);
            evaluatorRolesSet.add(candidateRole);
        });

        return evaluatorRolesSet;
        
    }

    public void deleteEvaluator(Long evaluatorId) {

        log.info("Excluindo avaliador com id={}", evaluatorId.toString());
        evaluatorsRepository.deleteEvaluatorById(evaluatorId); 

    }

    public Map<String, String> mapGuidstoNames(EvaluatorsNamesRequestDto evaluatorsNamesRequestDto) throws IOException {

        List<EvaluatorOrganizationDto> organizationsList = acessoCidadaoService.findOrganizationsFromOrganogramaAPI();

        Map<String, String> organizationsNamesList = organizationsList.stream()
            .filter((org) -> evaluatorsNamesRequestDto.getOrganizationsGuidList().contains(org.getGuid()))
            .collect(Collectors.toUnmodifiableMap((org) -> org.getGuid(), (org) -> org.getName()));

        List<EvaluatorSectionDto> sectionsList = new ArrayList<EvaluatorSectionDto>();
        
        evaluatorsNamesRequestDto.getOrganizationsGuidList().iterator().forEachRemaining((orgGuid) -> 
            {
                try {
                    acessoCidadaoService.findSectionsFromOrganogramaAPI(orgGuid).forEach(sectionsList::add);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        Map<String, String> sectionsNamesList = sectionsList.stream()
            .filter((section) -> evaluatorsNamesRequestDto.getSectionsGuidList().contains(section.getGuid()))
            .collect(Collectors.toUnmodifiableMap((section) -> section.getGuid(), (section) -> section.getName()));

        List<EvaluatorRoleDto> rolesList = new ArrayList<EvaluatorRoleDto>();

        evaluatorsNamesRequestDto.getSectionsGuidList().iterator().forEachRemaining((unitGuid) -> 
            {
                try {
                    acessoCidadaoService.findRolesFromAcessoCidadaoAPI(unitGuid).forEach(rolesList::add);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        Map<String, String> rolesNamesList = rolesList.stream()
            .filter((role) -> evaluatorsNamesRequestDto.getRolesGuidList().contains(role.getGuid()))
            .collect(Collectors.toUnmodifiableMap((role) -> role.getGuid(), (role) -> role.getName()));

        Map<String, String> evaluatorsNamesMap = new HashMap<String, String>();

        evaluatorsNamesMap.putAll(organizationsNamesList);
        evaluatorsNamesMap.putAll(sectionsNamesList);
        evaluatorsNamesMap.putAll(rolesNamesList);

        return evaluatorsNamesMap;

    }
    
}
