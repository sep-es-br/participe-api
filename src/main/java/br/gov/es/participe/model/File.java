package br.gov.es.participe.model;

import java.io.Serializable;

import org.neo4j.ogm.annotation.*;

import br.gov.es.participe.controller.dto.FileDto;

@NodeEntity
public class File extends Entity implements Serializable {

    private String mimeType;

    private String url;

    private String name;

    private String subtype;

    @Relationship(type = "IS_BACKGROUND_IMAGE_OF", direction = Relationship.OUTGOING)
    private Conference conferenceBackGround;

    @Relationship(type = "IS_CALENDAR_IMAGE_OF", direction = Relationship.OUTGOING)
    private Conference conferenceCalendarImage;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File() {}

    public File(FileDto fileDto) {
        if (fileDto == null) return;

        setId(fileDto.getId());
        this.name = fileDto.getName();
        this.url = fileDto.getUrl();
        this.mimeType = fileDto.getMimeType();
        this.subtype = fileDto.getSubtype();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Conference getConferenceBackGround() {
        return conferenceBackGround;
    }

    public File setConferenceBackGround(Conference conferenceBackGround) {
        this.conferenceBackGround = conferenceBackGround;
        return this;
    }

    public Conference getConferenceCalendarImage() {
        return conferenceCalendarImage;
    }

    public File setConferenceCalendarImage(Conference conferenceCalendarImage) {
        this.conferenceCalendarImage = conferenceCalendarImage;
        return this;
    }

}
