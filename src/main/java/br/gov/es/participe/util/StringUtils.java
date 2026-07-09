package br.gov.es.participe.util;

import java.text.Normalizer;

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

    public Boolean compareIfAContainsB(String a, String b) {
        return replaceSpecialCharacters(a).contains(replaceSpecialCharacters(b));
    }
    
    public static String apocClean(String text){
         if (text == null) {
            return null;
        }

        // Remove acentos
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Minúsculas
        normalized = normalized.toLowerCase();

        // Mantém apenas letras e números
        normalized = normalized.replaceAll("[^\\p{Alnum}]", "");

        return normalized;
    }
}
