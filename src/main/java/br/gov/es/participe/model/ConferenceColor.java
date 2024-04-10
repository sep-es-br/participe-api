package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.ConferenceColorDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
@NodeEntity
public class ConferenceColor extends Entity implements Serializable {
    @Relationship(type = "CONFERENCE_COLOR", direction = Relationship.OUTGOING )
    private Conference conference;
    private String background;
    private String accentColor;
    private String fontColor;
    private String cardFontColor;
    private String cardFontColorHover;
    private String cardColor;
    private String cardColorHover;
    private String cardBorderColor;
    private String borderColor;
    private String typeBackgroundColor;

    public ConferenceColor(){
    }

    public ConferenceColor(Conference conference, ConferenceColorDto conferenceColorDto) {
        this.conference = conference;
        this.background = conferenceColorDto.getBackground();
        this.accentColor = conferenceColorDto.getAccentColor();
        this.fontColor = conferenceColorDto.getFontColor();
        this.cardFontColor = conferenceColorDto.getCardFontColor();
        this.cardFontColorHover = conferenceColorDto.getCardFontColorHover();
        this.cardColor = conferenceColorDto.getCardColor();
        this.cardColorHover = conferenceColorDto.getCardColorHover();
        this.cardBorderColor = conferenceColorDto.getCardBorderColor();
        this.borderColor = conferenceColorDto.getBorderColor();
        this.typeBackgroundColor = conferenceColorDto.getTypeBackgroundColor();
    }
    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getCardFontColor() {
        return cardFontColor;
    }

    public void setCardFontColor(String cardFontColor) {
        this.cardFontColor = cardFontColor;
    }

    public String getCardFontColorHover() {
        return cardFontColorHover;
    }

    public void setCardFontColorHover(String cardFontColorHover) {
        this.cardFontColorHover = cardFontColorHover;
    }

    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public String getCardColorHover() {
        return cardColorHover;
    }

    public void setCardColorHover(String cardColorHover) {
        this.cardColorHover = cardColorHover;
    }

    public String getCardBorderColor() {
        return cardBorderColor;
    }

    public void setCardBorderColor(String cardBorderColor) {
        this.cardBorderColor = cardBorderColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getTypeBackgroundColor() {
        return typeBackgroundColor;
    }

    public void setTypeBackgroundColor(String typeBackgroundColor) {
        this.typeBackgroundColor = typeBackgroundColor;
    }
}
