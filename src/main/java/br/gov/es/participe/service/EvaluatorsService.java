package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluatorRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.exception.ParticipeServiceException;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;
import br.gov.es.participe.repository.EvaluatorsRepository;

@Service
public class EvaluatorsService {
    
    @Autowired
    private EvaluatorsRepository evaluatorsRepository;

    private static final Logger log = LoggerFactory.getLogger(EvaluatorsService.class);

    public List<EvaluatorResponseDto> findAllEvaluators() {

        List<EvaluatorResponseDto> evaluatorsList = evaluatorsRepository.findAllEvaluators();

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
            Optional<Section> section = evaluatorsRepository.findSectionByGuid(guid);

            if(section.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste setor.");
            } else {
                Section newSection = new Section(guid);
                newSection.setOrganization(organization);
                evaluatorsRepository.save(newSection);
                evaluatorSectionsSet.add(newSection);
            }
        });

        return evaluatorSectionsSet;
    }

    private Set<Section> updateSections(List<String> sectionsGuid, Organization organization) {

        List<Section> evaluatorSectionsList = evaluatorsRepository.findAllSectionsWithRelationshipToOrganization(organization.getId());

        evaluatorSectionsList.iterator().forEachRemaining((section) -> evaluatorsRepository.deleteById(section.getId()));

        Set<Section> evaluatorSectionsSet = new HashSet<Section>();

        sectionsGuid.iterator().forEachRemaining((guid) -> {
            Section candidateSection = evaluatorsRepository.findSectionByGuid(guid)
                .orElse(new Section(guid));
            
            candidateSection.setOrganization(organization);
            evaluatorsRepository.save(candidateSection);
            evaluatorSectionsSet.add(candidateSection);
        });

        return evaluatorSectionsSet;
    }
    
    private Set<Role> persistRoles(List<String> rolesGuid, Set<Section> evaluatorSections) {

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(rolesGuid.contains("all")){
            evaluatorRolesSet.add(new Role("all"));
            return evaluatorRolesSet;
        }

        rolesGuid.iterator().forEachRemaining((guid_lotacao) -> {

            String guid = guid_lotacao.split(":")[0];
            String lotacao = guid_lotacao.split(":")[1];

            Optional<Role> role = evaluatorsRepository.findRoleByGuid(guid);

            if(role.isPresent()){
                throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste papel.");
            } else {
                Role newRole = new Role(guid);
                Section targetSection = new ArrayList<Section>(evaluatorSections).stream().filter(
                    (section) -> section.getGuid().equals(lotacao))
                    .findFirst()
                    .get();
                newRole.setSection(targetSection);
                evaluatorsRepository.save(newRole);
                evaluatorRolesSet.add(newRole);
            }
        });

        return evaluatorRolesSet;
    }

    private Set<Role> updateRoles(List<String> rolesGuid, Set<Section> evaluatorSections) {

        evaluatorSections.iterator().forEachRemaining((section) -> {
            List<Role> evaluatorRolesList = evaluatorsRepository.findAllRolesWithRelationshipToSection(section.getId());
            evaluatorRolesList.iterator().forEachRemaining((role) -> evaluatorsRepository.deleteById(role.getId()));
        });

        Set<Role> evaluatorRolesSet = new HashSet<Role>();

        if(rolesGuid.contains("all")){
            evaluatorRolesSet.add(new Role("all"));
            return evaluatorRolesSet;
        }

        rolesGuid.iterator().forEachRemaining((guid_lotacao) -> {
            String guid = guid_lotacao.split(":")[0];
            String lotacao = guid_lotacao.split(":")[1];

            Role candidateRole = evaluatorsRepository.findRoleByGuid(guid)
                .orElse(new Role(guid));

            Section targetSection = new ArrayList<Section>(evaluatorSections).stream().filter(
                (section) -> section.getGuid().equals(lotacao))
                .findFirst()
                .get();
            
            candidateRole.setSection(targetSection);
            evaluatorsRepository.save(candidateRole);
            evaluatorRolesSet.add(candidateRole);
        });

        return evaluatorRolesSet;
    }

    public void deleteEvaluator(Long evaluatorId) {

        evaluatorsRepository.deleteEvaluatorById(evaluatorId);

    }
    
}
