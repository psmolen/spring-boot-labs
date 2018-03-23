package io.pivotal.workshop.codesnippetmanagerjdbc.models;

public class NewSnippet {

    public final String title;
    public final String code;

    public NewSnippet(String title, String code) {
        this.title = title;
        this.code = code;
    }

    // needed for jackson parsing
    private NewSnippet() {
        this(null, null);
    }
}
