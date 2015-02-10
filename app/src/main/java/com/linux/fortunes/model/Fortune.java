package com.linux.fortunes.model;

import java.io.Serializable;

/**
 * Created by julio on 12/12/14.
 */
public class Fortune implements Serializable {
    private String fortune;
    private String author;
    private long id;

    public Fortune(String fortune, String author) {
        this.fortune = fortune;
        this.author = author;
    }

    public String getFortune() {
        return fortune;
    }

    public String getAuthor() {
        return author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return fortune + "\n" + author;
    }
}
