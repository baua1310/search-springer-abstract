package de.uniregensburg.springer;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private List<Article> articles = new ArrayList<Article>();

    public Search(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> findInAbstract(String query) {
        List<Article> result = new ArrayList<Article>();

        for (Article article : articles) {
            boolean first = false, second = false, third = false, forth = false, fifth = false;
            String aabstract = article.getAbstract();
            aabstract = aabstract.toLowerCase();
            // TODO: use query string instead of if contains statements
            if (aabstract.contains("alternativ") || aabstract.contains("future") || aabstract.contains("zukunft") || aabstract.contains("zukünftig") || aabstract.contains("entwurf") || aabstract.contains("draft") || aabstract.contains("konzept") || aabstract.contains("concept") || aabstract.contains("model")) {
                first = true;
            }
            if (aabstract.contains("online") || aabstract.contains("internet") || aabstract.contains("web")) {
                second = true;
            }
            if (aabstract.contains("werbung") || aabstract.contains("advertising") || aabstract.contains("ad") || aabstract.contains("tracking")) {
                third = true;
            }
            if (aabstract.contains("souverän") || aabstract.contains("sovereignty") || aabstract.contains("selbstbestimmung") || aabstract.contains("self-determination") || aabstract.contains("self determination") || aabstract.contains("choice-driven") || aabstract.contains("choice driven")) {
                forth = true;
            }
            if (aabstract.contains("personenbezogene daten") || aabstract.contains("personal data") || aabstract.contains("personal information") || aabstract.contains("datenschutz") || aabstract.contains("privacy") || aabstract.contains("pii")) {
                fifth = true;
            }
            if (first && second && third && forth && fifth) {
                result.add(article);
            }
        }


        return result;
    }

}
