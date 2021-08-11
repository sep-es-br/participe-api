package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Channel;

public class ChannelDto {
    private Long id;
    private String name;
    private String url;

    public ChannelDto() {
    }

    public ChannelDto(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.url = channel.getUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
