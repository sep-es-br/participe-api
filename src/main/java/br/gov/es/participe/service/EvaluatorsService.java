package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluatorDto;
import br.gov.es.participe.controller.dto.EvaluatorParamDto;
import br.gov.es.participe.exception.EvaluationSectionsNotFoundException;
import br.gov.es.participe.exception.ParticipeServiceException;
import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;
import br.gov.es.participe.repository.EvaluatorsRepository;

@Service
public class EvaluatorsService {
    
    @Autowired
    private EvaluatorsRepository evaluatorsRepository;

    private static final Logger log = LoggerFactory.getLogger(EvaluatorsService.class);

    public List<Evaluator> findAllEvaluators() {

        List<Evaluator> evaluatorsList = new ArrayList<Evaluator>();

        evaluatorsRepository.findAll().iterator().forEachRemaining(evaluatorsList::add);

        return evaluatorsList;

    }

    public EvaluatorDto saveEvaluator(EvaluatorParamDto evaluatorParamDto) {

        // Cria o dto de avaliador

        EvaluatorDto newEvaluatorDto = new EvaluatorDto();

        // Persiste organizacao no banco, adiciona guid no dto

        Organization evaluatorOrganization = this.persistOrganization(evaluatorParamDto.getOrganizationGuid());

        newEvaluatorDto.setOrganizationGuid(evaluatorOrganization.getGuid());

        // Persiste seções no banco, adiciona guids no dto

        String evaluatorDtoSections = "";

        Stream<String> sectionsGuidArray = Arrays.stream(evaluatorParamDto.getSectionsGuid().split(","));

        sectionsGuidArray.iterator().forEachRemaining((guid) -> {
            Section evaluatorSection = this.persistSection(guid);
            evaluatorDtoSections.concat(evaluatorSection.getGuid());
            if(sectionsGuidArray.iterator().hasNext()){
                evaluatorDtoSections.concat(",");
            }
        });

        newEvaluatorDto.setSectionsGuid(evaluatorDtoSections);

        // Persiste papeis no banco, adiciona guids no dto

        String evaluatorDtoRoles = "";

        Stream<String> rolesGuidArray = Arrays.stream(evaluatorParamDto.getRolesGuid().split(","));

        rolesGuidArray.iterator().forEachRemaining((guid) -> {
            Role evaluatorRole = this.persistRole(guid);
            evaluatorDtoRoles.concat(evaluatorRole.getGuid());
            if(rolesGuidArray.iterator().hasNext()){
                evaluatorDtoRoles.concat(",");
            }
        });

        newEvaluatorDto.setRolesGuid(evaluatorDtoRoles);

        // Retorna o dto

        return newEvaluatorDto;
    }

    // public Evaluator updateEvaluator(EvaluatorParamDto evaluatorParamDto, Long evalSectId) {

    //     Evaluator evaluator = evaluatorsRepository.findById(evalSectId).orElseThrow(() -> new EvaluationSectionsNotFoundException(evalSectId));

    //     evaluator.setSections(evaluatorParamDto.getSectionsGuid());
    //     evaluator.setServers(evaluatorParamDto.getServersGuid());

    //     evaluatorsRepository.save(evaluator);

    //     return evaluator;
    // }

    public void deleteEvaluator(Long evalSectId) {

        evaluatorsRepository.deleteById(evalSectId);

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
    
    private Section persistSection(String sectionGuid) {
    
        Optional<Section> section = evaluatorsRepository.findSectionByGuid(sectionGuid);

        if(section.isPresent()){
            throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste setor.");
        } else {
            Section newSection = new Section(sectionGuid);
            evaluatorsRepository.save(newSection);
            return newSection;
        }
    }
    
    private Role persistRole(String roleGuid) {

        Optional<Role> Role = evaluatorsRepository.findRoleByGuid(roleGuid);

        if(Role.isPresent()){
            throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid deste papel.");
        } else {
            Role newRole = new Role(roleGuid);
            evaluatorsRepository.save(newRole);
            return newRole;
        }
    }
}
