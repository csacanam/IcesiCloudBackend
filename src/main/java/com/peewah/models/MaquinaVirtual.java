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
@DatabaseTable(tableName = "MaquinaVirtual")
public class MaquinaVirtual
{

    //Nombres de los campos
    public static final String USERNAME_FIELD = "username_id";
    public static final String NOMBRE_FIELD = "nombre";
    public static final String SISTEMAOPERATIVO_FIELD = "sistemaOperativo_id";

    //Campos
    @Expose
    @DatabaseField(generatedIdSequence = "mv_seq")
    private int id;

    @Expose
    @DatabaseField(canBeNull = false)
    private String nombre;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Usuario username;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private SistemaOperativo sistemaOperativo;

    @ForeignCollectionField(eager = false)
    private Collection<MaquinaApp> maquinasApps = new ArrayList<>();


    public MaquinaVirtual()
    {

    }

    public Usuario getUsername()
    {
        return username;
    }

    public void setUsername(Usuario username)
    {
        this.username = username;
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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

        public Collection<MaquinaApp> getMaquinasApps()
    {
        return maquinasApps;
    }

    public void setMaquinasApps(Collection<MaquinaApp> maquinasApps)
    {
        this.maquinasApps = maquinasApps;
    }

}
