/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.service;

import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.repository.CheckedInAtRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author desenvolvimento
 */
@Service
public class AuthorityCredentialService {
    
    @Autowired
    private CheckedInAtRepository checkedInAtRepository;
    
    public Optional<CheckedInAt> toggleAnnounced(final Long idCheckedIn) {
        return checkedInAtRepository.toggleAnnounced(idCheckedIn);
    }
    
    public Optional<CheckedInAt> toggleToAnnounce(final Long idCheckedIn) {
        return checkedInAtRepository.toggleToAnnounce(idCheckedIn);
    }
    
    
    
}
