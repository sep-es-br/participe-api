package br.gov.es.participe.util.domain;

public enum CommentTypeType {
    PRESENTIAL("Presential", "pre"),
    REMOTE("Remote", "com");

    public final String completeName;
    public final String leanName;

    private CommentTypeType(String completeName, String leanName) {
        this.completeName = completeName;
        this.leanName = leanName;
    }

    public String getLeanNameByCompleteName(String completeName) {
        if(!(completeName==null || completeName.equals(""))) {
            for (CommentTypeType e : values()) {
                if(e.completeName.equals(completeName)) {
                    return e.leanName;
                }
            }
        }
        return "";
    }

    public String getCompleteNameFromLeanName(String leanName) {
        for (CommentTypeType e : values()) {
            if(e.leanName.equals(leanName)) {
                return e.completeName;
            }
        }
        return "";
    }
}
