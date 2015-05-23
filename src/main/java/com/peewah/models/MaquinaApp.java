/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.peewah.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author csacanam
 */
@DatabaseTable(tableName = "MaquinaApp")
public class MaquinaApp
{

    //Nombres de los campos
    public static final String MACHINE_FIELD = "maquina_id";

    @DatabaseField(generatedIdSequence = "ma_seq")
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private MaquinaVirtual maquina;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private App app;

    public MaquinaApp()
    {

    }

    public MaquinaApp(MaquinaVirtual maquina, App app)
    {
        this.maquina = maquina;
        this.app = app;
    }

    public MaquinaVirtual getMaquina()
    {
        return maquina;
    }

    public void setMaquina(MaquinaVirtual maquina)
    {
        this.maquina = maquina;
    }

    public App getApp()
    {
        return app;
    }

    public void setApp(App app)
    {
        this.app = app;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

}
