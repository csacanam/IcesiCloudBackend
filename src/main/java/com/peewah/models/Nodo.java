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
@DatabaseTable(tableName = "Nodo")
public class Nodo
{

    @Expose
    @DatabaseField(generatedIdSequence = "nodo_seq")
    private int id;

    @Expose
    @DatabaseField(canBeNull = false)
    private String nombre;

    @Expose
    @DatabaseField(canBeNull = false)
    private String ipPrivada;

    @Expose
    @DatabaseField(canBeNull = false)
    private String ipPublica;

    @Expose
    @DatabaseField(canBeNull = false)
    private String interfazPuente;

    @Expose
    @DatabaseField(canBeNull = false)
    private String mascaraRed;

    @Expose
    @DatabaseField(canBeNull = false)
    private String cantidadMemoria;

    @Expose
    @DatabaseField(canBeNull = false)
    private String cantidadCPU;

    @Expose
    @DatabaseField(canBeNull = false)
    private String parametrosJSON;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private MaquinaVirtual maquinaVirtual;

    public Nodo()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getIpPrivada()
    {
        return ipPrivada;
    }

    public void setIpPrivada(String ipPrivada)
    {
        this.ipPrivada = ipPrivada;
    }

    public String getIpPublica()
    {
        return ipPublica;
    }

    public void setIpPublica(String ipPublica)
    {
        this.ipPublica = ipPublica;
    }

    public String getInterfazPuente()
    {
        return interfazPuente;
    }

    public void setInterfazPuente(String interfazPuente)
    {
        this.interfazPuente = interfazPuente;
    }

    public String getMascaraRed()
    {
        return mascaraRed;
    }

    public void setMascaraRed(String mascaraRed)
    {
        this.mascaraRed = mascaraRed;
    }

    public String getCantidadMemoria()
    {
        return cantidadMemoria;
    }

    public void setCantidadMemoria(String cantidadMemoria)
    {
        this.cantidadMemoria = cantidadMemoria;
    }

    public String getCantidadCPU()
    {
        return cantidadCPU;
    }

    public void setCantidadCPU(String cantidadCPU)
    {
        this.cantidadCPU = cantidadCPU;
    }

    public MaquinaVirtual getMaquinaVirtual()
    {
        return maquinaVirtual;
    }

    public void setMaquinaVirtual(MaquinaVirtual maquinaVirtual)
    {
        this.maquinaVirtual = maquinaVirtual;
    }

    public String getParametrosJSON()
    {
        return parametrosJSON;
    }

    public void setParametrosJSON(String parametrosJSON)
    {
        this.parametrosJSON = parametrosJSON;
    }

    
}
