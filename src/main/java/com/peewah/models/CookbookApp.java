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
@DatabaseTable(tableName = "CookbookApp")
public class CookbookApp
{

    @DatabaseField(generatedIdSequence = "ca_seq")
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Cookbook cookbook;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private App app;

    public CookbookApp()
    {
    }

    public CookbookApp(Cookbook cookbook, App app)
    {
        this.cookbook = cookbook;
        this.app = app;
    }

    public Cookbook getCookbook()
    {
        return cookbook;
    }

    public void setCookbook(Cookbook cookbook)
    {
        this.cookbook = cookbook;
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
