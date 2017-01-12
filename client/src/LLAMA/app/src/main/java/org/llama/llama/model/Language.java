package org.llama.llama.model;

/**
 * Created by Daniel on 12.01.2016.
 */

public class Language implements Comparable<Language> {
    private String id;
    private String name;
    private String nativeName;

    public Language() {
    }

    public Language(String name, String nativeName) {
        this.name = name;
        this.nativeName = nativeName;
    }

    //<editor-fold desc="Properties">
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    @Override
    public int compareTo(Language other){
        return this.nativeName.compareToIgnoreCase(other.nativeName);
    }
    //</editor-fold>
}
