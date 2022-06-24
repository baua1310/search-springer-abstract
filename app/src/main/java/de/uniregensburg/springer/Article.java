package de.uniregensburg.springer;

public class Article {
    private String identifier;
    private String title;
    private String aabstract;
    private String doi;
    private String url;

    public Article(String identifier, String title, String aabstract, String doi, String url) {
        this.identifier = identifier;
        this.title = title;
        this.aabstract = aabstract;
        this.doi = doi;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Article [doi=" + doi + ", identifier=" + identifier + ", title=" + title
                + ", url=" + url + "]";
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return this.aabstract;
    }

    public void setAbstract(String aabstract) {
        this.aabstract = aabstract;
    }

    public String getDoi() {
        return this.doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    

}
