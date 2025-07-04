package br.gov.es.participe.service;

import java.io.IOException;
import java.util.ArrayList;
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

import br.gov.es.participe.controller.dto.EvaluatorRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesResponseDto;
import br.gov.es.participe.exception.EvaluatorForbiddenException;
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

    public Page<EvaluatorResponseDto> findAllEvaluators(
        String orgGuidFilter, 
        String sectionGuidFilter, 
        String roleGuidFilter, 
        Pageable pageable
    ) {

        Page<EvaluatorResponseDto> evaluatorsList = evaluatorsRepository.findAllEvaluators(
            orgGuidFilter, 
            sectionGuidFilter, 
            roleGuidFilter, 
            pageable
        );

        return evaluatorsList;

    }

    public String findOrganizationGuidBySectionOrRole(List<EvaluatorRoleDto> evaluatorRoleDto) {

        Set<String> organizationGuids = new HashSet<>();

        for( EvaluatorRoleDto evaluator: evaluatorRoleDto){
            Optional<Role> role = findRoleByGuid(evaluator.getGuid());
            if(role.isEmpty()){
                Optional<Section> section = findSectionWithNoRoleByGuid(evaluator.getLotacao());
                if(section.isPresent()){
                    organizationGuids.add(evaluatorsRepository.findOrganizationRelatedToSectionBySectionGuid(evaluator.getLotacao()).getGuid());
                }
            } else {
                organizationGuids.add(evaluatorsRepository.findOrganizationRelatedToRoleByRoleGuid(evaluator.getGuid()).getGuid());
            }

        }
        if(!organizationGuids.isEmpty()){;
            return String.join(",", organizationGuids);
        }else{
            throw new EvaluatorForbiddenException();
        }

    }

    private Optional<Section> findSectionWithNoRoleByGuid(String sectionGuid) {

        return evaluatorsRepository.findSectionWithNoRoleByGuid(sectionGuid);

    }

    private Optional<Role> findRoleByGuid(String roleGuid) {

        return evaluatorsRepository.findRoleByGuid(roleGuid);

    }

    public EvaluatorResponseDto saveEvaluator(EvaluatorRequestDto evaluatorRequestDto) {

        Organization evaluatorOrganization = this.persistOrganization(evaluatorRequestDto.getOrganization());

        Set<Section> evaluatorSections = this.persistSections(evaluatorRequestDto.getSections(), evaluatorOrganization);

        Set<Role> evaluatorRoles = this.persistRoles(evaluatorRequestDto.getRoles(), evaluatorSections);

        return new EvaluatorResponseDto(evaluatorOrganization, evaluatorSections, evaluatorRoles);

    }

    public EvaluatorResponseDto updateEvaluator(EvaluatorRequestDto evaluatorRequestDto, Long evaluatorId) {

        Organization evaluatorOrganization = evaluatorsRepository.findOrganizationById(evaluatorId)
            .orElseThrow(() -> new ParticipeServiceException("Organização não encontrada com o id fornecido"));
        
        evaluatorOrganization.setName(evaluatorRequestDto.getOrganization().getName());
        evaluatorsRepository.save(evaluatorOrganization);

        Set<Section> evaluatorSections = this.updateSections(evaluatorRequestDto.getSections(), evaluatorOrganization);

        Set<Role> evaluatorRoles = this.updateRoles(evaluatorRequestDto.getRoles(), evaluatorSections);

        return new EvaluatorResponseDto(evaluatorOrganization, evaluatorSections, evaluatorRoles);

    }

    private Organization persistOrganization(Organization org) {

        log.info("Persistindo organizacao com guid={}", org.getGuid());
        Optional<Organization> organization = evaluatorsRepository.findOrganizationByGuid(org.getGuid());

        if(organization.isPresent()){
            throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid desta organização.");
        } else {
            Organization newOrganization = new Organization(org.getGuid(),org.getName());
            evaluatorsRepository.save(newOrganization);
            return newOrganization;
        }

    }
    
    private Set<Section> persistSections(List<Section> sections, Organization organization) {

        Set<Section> evaluatorSectionsSet = new HashSet<Section>();

        sections.iterator().forEachRemaining((sect) -> {
            log.info("Persistindo setor com guid={}", sect.getGuid());
            Optional<Section> section = evaluatorsRepository.findSectionByGuid(sect.getGuid());

            if(section.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste setor.");
            } else {
                Section newSection = new Section(sect.getGuid(),sect.getName());
                log.info("Criando relacionamento entre setor={} e organizacao={}", sect.getGuid(), organization.getGuid());
                newSection.setOrganization(organization);
                evaluatorsRepository.save(newSection);
                evaluatorSectionsSet.add(newSection);
            }
        });

        return evaluatorSectionsSet;

    }

    private Set<Section> updateSections(List<Section> sections, Organization organization) {

        log.info("Buscando setores pertencentes a organizacao={}", organization.getId().toString());
        List<Section> evaluatorSectionsList = evaluatorsRepository.findAllSectionsWithRelationshipToOrganization(organization.getId());

        evaluatorSectionsList.iterator().forEachRemaining((section) -> evaluatorsRepository.deleteById(section.getId()));

        Set<Section> evaluatorSectionsSet = new HashSet<Section>();

        sections.iterator().forEachRemaining((section) -> {
            log.info("Buscando setor com guid={}", section.getGuid());
            Section candidateSection = evaluatorsRepository.findSectionByGuid(section.getGuid())
                .orElse(new Section(section.getGuid(), section.getName()));
            
            log.info("Criando relacionamento entre setor={} e organizacao={}", section.getGuid(), organization.getId().toString());
            candidateSection.setOrganization(organization);
            candidateSection.setName(section.getName());
            evaluatorsRepository.save(candidateSection);
            evaluatorSectionsSet.add(candidateSection);
        });

        return evaluatorSectionsSet;

    }
    
    private Set<Role> persistRoles(List<Role> roles, Set<Section> evaluatorSections) {

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(roles.stream().anyMatch(role -> "Todos".equals(role.getName()))){
            log.info("Nenhum guid foi fornecido, nenhum papel sera persistido no banco");
            evaluatorRolesSet.add(new Role());
            return evaluatorRolesSet;
        }

        roles.iterator().forEachRemaining((guid_lotacao_name) -> {

            String guid = guid_lotacao_name.getGuid();
            String lotacao = guid_lotacao_name.getLotacao();

            log.info("Persistindo papel com guid={}", guid);
            Optional<Role> role = evaluatorsRepository.findRoleByGuid(guid);

            if(role.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste papel.");
            } else {
                Role newRole = new Role(guid, guid_lotacao_name.getName());
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

    private Set<Role> updateRoles(List<Role> roles, Set<Section> evaluatorSections) {

        evaluatorSections.iterator().forEachRemaining((section) -> {
            log.info("Buscando papeis pertencentes ao setor={}", section.getId().toString());
            List<Role> evaluatorRolesList = evaluatorsRepository.findAllRolesWithRelationshipToSection(section.getId());
            evaluatorRolesList.iterator().forEachRemaining((role) -> evaluatorsRepository.deleteById(role.getId()));

            log.info("Removendo papeis sem relacionamento com nenhum setor");
            List<Role> rolesWithNoRelationshipList = evaluatorsRepository.findAllRolesWithNoRelationships();
            if(!rolesWithNoRelationshipList.isEmpty()) {
                rolesWithNoRelationshipList.iterator().forEachRemaining((role) -> evaluatorsRepository.deleteById(role.getId()));
            }
        });

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(roles.stream().anyMatch(role -> "Todos".equals(role.getName()))){
            log.info("Nenhum guid foi fornecido, nenhum papel sera persistido no banco");
            evaluatorRolesSet.add(new Role());
            return evaluatorRolesSet;
        }

        roles.iterator().forEachRemaining((guid_lotacao_name) -> {
            String guid = guid_lotacao_name.getGuid();
            String lotacao = guid_lotacao_name.getLotacao();

            log.info("Buscando papel com guid={}", guid);
            Role candidateRole = evaluatorsRepository.findRoleByGuid(guid)
                .orElse(new Role(guid,guid_lotacao_name.getName()));

            log.info("Buscando no banco setor com guid={}", lotacao);
            Section targetSection = new ArrayList<Section>(evaluatorSections).stream().filter(
                (section) -> section.getGuid().equals(lotacao))
                .findFirst()
                .get();
            
            log.info("Criando relacionamento entre papel={} e setor={}", guid, targetSection.getGuid());
            candidateRole.setSection(targetSection);
            candidateRole.setName(guid_lotacao_name.getName());
            evaluatorsRepository.save(candidateRole);
            evaluatorRolesSet.add(candidateRole);
        });

        return evaluatorRolesSet;
        
    }

    public void deleteEvaluator(Long evaluatorId) {

        log.info("Excluindo avaliador com id={}", evaluatorId.toString());
        evaluatorsRepository.deleteEvaluatorById(evaluatorId); 

    }

    public EvaluatorsNamesResponseDto mapGuidstoNames(EvaluatorsNamesRequestDto evaluatorsNamesRequestDto) throws IOException {

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

        EvaluatorsNamesResponseDto evaluatorsNamesMap = new EvaluatorsNamesResponseDto(sectionsNamesList, rolesNamesList);

        return evaluatorsNamesMap;

    }
    
}
