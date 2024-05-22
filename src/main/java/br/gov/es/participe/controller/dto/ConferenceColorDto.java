package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.ConferenceColor;

@QueryResult
public class ConferenceColorDto {

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
    private String cardLoginColor;

    public ConferenceColorDto() {
    }

    public ConferenceColorDto(ConferenceColor conferenceColor ) {
        this.background = conferenceColor.getBackground();
        this.accentColor = conferenceColor.getAccentColor();
        this.fontColor = conferenceColor.getFontColor();
        this.cardFontColor = conferenceColor.getCardFontColor();
        this.cardFontColorHover = conferenceColor.getCardFontColorHover();
        this.cardColor = conferenceColor.getCardColor();
        this.cardColorHover = conferenceColor.getCardColorHover();
        this.cardBorderColor = conferenceColor.getCardBorderColor();
        this.borderColor = conferenceColor.getBorderColor();
        this.typeBackgroundColor = conferenceColor.getTypeBackgroundColor();
        this.cardLoginColor = conferenceColor.getCardLoginColor();
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

    public String getCardLoginColor() {
        return cardLoginColor;
    }

    public void setCardLoginColor(String cardLoginColor) {
        this.cardLoginColor = cardLoginColor;
    }
}

