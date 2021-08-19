package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.ChannelDto;
import br.gov.es.participe.model.Channel;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChannelService {

  private final ChannelRepository channelRepository;

  @Autowired
  public ChannelService(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
  }

  public Set<Channel> saveChannelsMeeting(List<ChannelDto> channelsDto, Meeting meeting) {
    Set<Channel> storedChannels = meeting.getChannels();
    storedChannels.removeIf(
      channelSaved -> channelsDto.stream()
        .filter(dto -> dto.getId() != null)
        .noneMatch(channel -> channel.getId().equals(channelSaved.getId())));

    channelsDto.forEach(channel -> {
      if(channel.getId() == null) {
        Channel channelEntity = new Channel(channel.getName(), channel.getUrl());
        storedChannels.add(channelEntity);
      }

      Optional<Channel> channelFound = storedChannels.stream().filter(filtro -> {
        if(filtro.getId() == null) return false;

        return filtro.getId().equals(channel.getId());
      }).findFirst();

      if(!channelFound.isPresent()) {
        return;
      }

      Channel channelUpdated = channelFound.get();
      channelUpdated.setName(channel.getName());
      channelUpdated.setUrl(channel.getUrl());
    });

    this.channelRepository.saveAll(storedChannels);

    return storedChannels;
  }

  public Channel save(Channel channel) {
    return channelRepository.save(channel);
  }

}
