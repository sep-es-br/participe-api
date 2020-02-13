package br.gov.es.participe.service;

import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.repository.LocalityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocalityTypeService {

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    public List<LocalityType> findAll() {
        List<LocalityType> localityTypes = new ArrayList<>();

        localityTypeRepository
                .findAll()
                .iterator()
                .forEachRemaining(localityType -> localityTypes.add(localityType));

        return localityTypes;
    }

    public LocalityType find(Long id) {
        LocalityType localityType = localityTypeRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("domain.error.locality-type.not-found"));

        return localityType;
    }
}
