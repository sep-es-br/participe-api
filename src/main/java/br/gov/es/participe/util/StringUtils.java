package br.gov.es.participe.util;

public class StringUtils {
    public String replaceSpecialCharacters(String input) {
        String response = input;
        response = response.replaceAll("[âãäáàÂÃÄÁÀ]","a");
        response = response.replaceAll("[êëéèÊËÉÈ]","e");
        response = response.replaceAll("[îïíìÎÏÍÌ]","i");
        response = response.replaceAll("[ôõöóòÔÕÖÓÒ]","o");
        response = response.replaceAll("[ûüúùÛÜÚÙ]","u");
        response = response.replaceAll("[Çç]","c");
        response = response.toLowerCase();
        response = response.replaceAll("[^a-z0-9\\s]","");
        return response;
    }
}
