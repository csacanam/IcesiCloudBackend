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
@DatabaseTable(tableName = "SistemaOperativo")
public class SistemaOperativo
{

    @DatabaseField(id = true, canBeNull = false)
    private String nombre;
    
    @DatabaseField(canBeNull = false)
    private String nombreBox;

    @ForeignCollectionField(eager = false)
    private Collection<MaquinaVirtual> maquinasVirtuales = new ArrayList<>();
    
    @ForeignCollectionField(eager = false)
    private Collection<App> apps = new ArrayList<>();

    public SistemaOperativo()
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

    public Collection<MaquinaVirtual> getMaquinasVirtuales()
    {
        return maquinasVirtuales;
    }

    public void setMaquinasVirtuales(Collection<MaquinaVirtual> maquinasVirtuales)
    {
        this.maquinasVirtuales = maquinasVirtuales;
    }

    public Collection<App> getApps()
    {
        return apps;
    }

    public void setApps(Collection<App> apps)
    {
        this.apps = apps;
    }

    public String getNombreBox()
    {
        return nombreBox;
    }

    public void setNombreBox(String nombreBox)
    {
        this.nombreBox = nombreBox;
    }
    
    

    @Override
    public String toString()
    {
        return nombre;
    }

}
