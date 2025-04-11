package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.ConferenceColorDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.ConferenceColor;
import br.gov.es.participe.repository.ConferenceColorRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConferenceColorService {

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ConferenceColorRepository conferenceColorRepository;

    public Conference findByIdConference(Long id){
        return conferenceRepository.findById(id).orElse(null);
    }

    public ConferenceColor findByConferenceColor(Long id){
        return conferenceColorRepository.findByConferenceColor(id).orElse(null);
    }

    public void update(ConferenceColor conferenceColor, ConferenceColorDto conferenceColorDto){
        conferenceColor.setBackground(conferenceColorDto.getBackground());
        conferenceColor.setAccentColor(conferenceColorDto.getAccentColor());
        conferenceColor.setFontColor(conferenceColorDto.getFontColor());
        conferenceColor.setCardFontColor(conferenceColorDto.getCardFontColor());
        conferenceColor.setCardFontColorHover(conferenceColorDto.getCardFontColorHover());
        conferenceColor.setCardColor(conferenceColorDto.getCardColor());
        conferenceColor.setCardColorHover(conferenceColorDto.getCardColorHover());
        conferenceColor.setCardBorderColor(conferenceColorDto.getCardBorderColor());
        conferenceColor.setBorderColor(conferenceColorDto.getBorderColor());
        conferenceColor.setTypeBackgroundColor(conferenceColorDto.getTypeBackgroundColor());
        conferenceColor.setCardLoginColor(conferenceColorDto.getCardLoginColor());

        conferenceColorRepository.save(conferenceColor);
    }

    public void save(Conference conference, ConferenceColorDto conferenceColorDto){
        ConferenceColor conferenceColor = new ConferenceColor(conference, conferenceColorDto);
        conferenceColorRepository.save(conferenceColor);
    }
}
