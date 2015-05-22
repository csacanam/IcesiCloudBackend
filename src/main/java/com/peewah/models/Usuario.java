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
@DatabaseTable(tableName = "Usuario")
public class Usuario
{

    @Expose
    @DatabaseField(id = true, canBeNull = false)
    private String username;

    @Expose
    @DatabaseField(canBeNull = false)
    private String password;

    @Expose
    @DatabaseField(canBeNull = false)
    private String nombre;

    @ForeignCollectionField(eager = false)
    private Collection<MaquinaVirtual> maquinasVirtuales = new ArrayList<>();

    public Usuario()
    {
        //parentDao.assignEmptyForeignCollection(parent, "maquinasVirtuales");
        //Con esto, todo lo que se agrege a la colección también se agregará a la bd
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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


    
}
