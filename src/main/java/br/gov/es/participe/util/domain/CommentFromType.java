package br.gov.es.participe.util.domain;

public enum CommentFromType {
    
    PRESENTIAL("Presential", "pres"),
    REMOTE("Remote", "rem");

    public final String completeName;
    public final String leanName;

    private CommentFromType(String completeName, String leanName) {
        this.completeName = completeName;
        this.leanName = leanName;
    }

    public String getLeanNameByCompleteName(String completeName) {
        if(!(completeName==null || completeName.equals(""))) {
            for (CommentFromType e : values()) {
                if(e.completeName.equals(completeName)) {
                    return e.leanName;
                }
            }
        }
        return null;
    }

    public String getCompleteNameFromLeanName(String leanName) {
        for (CommentFromType e : values()) {
            if(e.leanName.equals(leanName)) {
                return e.completeName;
            }
        }
        return "";
    }

}