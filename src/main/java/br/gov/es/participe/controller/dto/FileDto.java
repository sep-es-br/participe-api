package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.File;

public class FileDto {

    private Long id;
    private String name;
    private String url;
    private String mimeType;
    private String subtype;

    public FileDto() {}

    public FileDto(File file) {
    	if (file == null) {
    		return;
    	}
    	
        this.id = file.getId();
        this.name = file.getName();
        this.url = file.getUrl();
        this.mimeType = file.getMimeType();
        this.subtype = file.getSubtype();
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
}
