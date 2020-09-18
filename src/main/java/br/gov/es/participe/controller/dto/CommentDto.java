package br.gov.es.participe.controller.dto;

import java.util.Date;

import br.gov.es.participe.model.Comment;

public class CommentDto {

	private Long id;
	private Date time;
	private String text;
	private String type;
	private String status;
	private String from;
	private Integer likes;
	private PersonDto personLiked;
	
	public CommentDto() {
	}
	
	public CommentDto(Comment comment, boolean front) {
		
		this.id = comment.getId();
		this.text = comment.getText();
		
		if(!front) {
			this.type = comment.getType();
			this.from = comment.getFrom();
			this.status = comment.getStatus();
			this.time = comment.getTime();
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public PersonDto getPersonLiked() {
		return personLiked;
	}

	public void setPersonLiked(PersonDto person) {
		this.personLiked = person;
	}
}
