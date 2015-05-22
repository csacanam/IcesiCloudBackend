/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.peewah.distribuidosfinal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

/**
 *
 * @author csacanam
 */

public class JsonTransformer implements ResponseTransformer
{    
    @Override
    public String render(Object model)
    {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(model);
    }
    
}
