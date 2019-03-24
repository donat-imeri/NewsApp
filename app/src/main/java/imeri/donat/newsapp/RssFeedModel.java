package imeri.donat.newsapp;

import java.io.Serializable;

public class RssFeedModel implements Serializable {
    public String title;
    public String link;
    public String description;

    public RssFeedModel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }
}
