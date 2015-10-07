/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author SasPes
 */
public class Utils {

    public static String messageJosn(MessageTags message, String messageText) {
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty(message.name().toLowerCase(), messageText);
        messageJson.addProperty("time", df.format(date));

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        return gson.toJson(messageJson);
    }

}
