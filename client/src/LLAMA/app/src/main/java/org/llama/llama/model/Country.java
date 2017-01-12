package org.llama.llama.model;

/**
 * Created by Daniel on 12.01.2016.
 */

public class Country implements Comparable<Country> {
    private String id;
    private String iso3;
    private String name;
    private String nativeName;

    public Country() {
    }

    public Country(String name, String nativeName) {
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

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
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
    public int compareTo(Country other){
        return this.nativeName.compareToIgnoreCase(other.nativeName);
    }
    //</editor-fold>
}
