package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PortalServerService {

    @Autowired
    private PortalServerRepository portalServerRepository;

    public Optional<PortalServer> findByUrl(String url) {
        return portalServerRepository.findByUrl(url);
    }

    @Transactional
    public PortalServer save(PortalServer portalServer) {
        return portalServerRepository.save(portalServer);
    }

    public Optional<PortalServer> findByIdConference(Long id) {
        return portalServerRepository.findByIdConference(id);
    }
}
