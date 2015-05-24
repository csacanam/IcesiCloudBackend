/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.peewah.models;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author csacanam
 */
@DatabaseTable(tableName = "App")
public class App
{

    @Expose
    @DatabaseField(id = true, canBeNull = false)
    private String nombre;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private SistemaOperativo sistemaOperativo;

    @ForeignCollectionField(eager = false)
    private Collection<MaquinaApp> maquinasApps = new ArrayList<>();

    @ForeignCollectionField(eager = false)
    private Collection<CookbookApp> cookbooksApps = new ArrayList<>();

    public App()
    {
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public SistemaOperativo getSistemaOperativo()
    {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(SistemaOperativo sistemaOperativo)
    {
        this.sistemaOperativo = sistemaOperativo;
    }

    public Collection<MaquinaApp> getMaquinasApps()
    {
        return maquinasApps;
    }

    public void setMaquinasApps(Collection<MaquinaApp> maquinasApps)
    {
        this.maquinasApps = maquinasApps;
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
