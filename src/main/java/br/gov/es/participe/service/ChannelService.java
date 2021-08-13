package br.gov.es.participe.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.ChannelDto;
import br.gov.es.participe.model.Channel;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.repository.ChannelRepository;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    public Set<Channel> saveChannelsMeeting(List<ChannelDto> channelsDto, Meeting meeting) {
        Set<Channel> channelsSaved = meeting.getChannels();
        channelsSaved.removeIf(
                channelSaved -> channelsDto.stream().noneMatch(filtro -> filtro.getId().equals(channelSaved.getId())));

        channelsDto.forEach(channel -> {
            if (channel.getId() == null) {
                Channel channelEntity = new Channel(channel.getName(), channel.getUrl());
                channelsSaved.add(channelEntity);
            }

            Optional<Channel> channelFinded = channelsSaved.stream().filter(filtro -> {
                if(filtro.getId() == null) return false;

                return filtro.getId().equals(channel.getId());
            }).findFirst();

            if (!channelFinded.isPresent())
                return;

            Channel channelUpdated = channelFinded.get();
            channelUpdated.setName(channel.getName());
            channelUpdated.setUrl(channel.getUrl());
        });

        this.channelRepository.saveAll(channelsSaved);

        return channelsSaved;
    }

    public Channel save(Channel channel) {
        return channelRepository.save(channel);
    }

}
