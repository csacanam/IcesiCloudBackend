/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.peewah.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author csacanam
 */
@DatabaseTable(tableName = "Cookbook")
public class Cookbook
{

    @DatabaseField(id = true, canBeNull = false)
    private String ruta;

    @ForeignCollectionField(eager = false)
    private Collection<CookbookApp> cookbooksApps = new ArrayList<>();

    public Cookbook()
    {
    }

    public String getRuta()
    {
        return ruta;
    }

    public void setRuta(String ruta)
    {
        this.ruta = ruta;
    }

    public Collection<CookbookApp> getCookbooksApps()
    {
        return cookbooksApps;
    }

    public void setCookbooksApps(Collection<CookbookApp> cookbooksApps)
    {
        this.cookbooksApps = cookbooksApps;
    }

}
