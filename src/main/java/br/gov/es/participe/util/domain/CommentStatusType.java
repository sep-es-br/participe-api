package br.gov.es.participe.util.domain;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public enum CommentStatusType {
    PENDING("Pending", "pen"),
    FILED("Filed", "arq"),
    REMOVED("Removed", "rem"),
    PUBLISHED("Published", "pub"),
    ALL("All", "");

    public final String completeName;
    public final String leanName;

    private CommentStatusType(String completeName, String leanName) {
        this.completeName = completeName;
        this.leanName = leanName;
    }

    public String[] getLeanNameByCompleteName(String completeName) {
        ArrayList<String> response = new ArrayList<>();
        if(!(completeName==null || completeName.equals(""))) {
        	if(completeName.equals("All")) {
        		return new String[] {"pen" ,"arq", "pub"};
        	}
        	for (CommentStatusType e : values()) {
                if(e.completeName.equals(completeName)) {
                    response.add(e.leanName);
                    String[] responseArray = new String[response.size()];
                    return response.toArray(responseArray);
                }
            }
        }
        response.add("pen");
        String[] responseArray = new String[response.size()];
        return response.toArray(responseArray);
    }

    public String getCompleteNameFromLeanName(String leanName) {
        for (CommentStatusType e : values()) {
            if(e.leanName.equals(leanName)) {
                return e.completeName;
            }
        }
        return "";
    }

    public String isValidStatusType(String completeName) {
        if(!(completeName==null || completeName.equals(""))) {
            for (CommentStatusType e : values()) {
                if(e.completeName.equals(completeName)) {
                    return e.leanName;
                }
            }
        }
        return "";
    }
}
