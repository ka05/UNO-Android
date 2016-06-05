package com.envative.uno.models;

import com.google.gson.JsonObject;

/**
 * Created by clay on 6/2/16.
 */
public class Card {

    public String svgName = "";
    public String cardName = "";
    public String value = "";
    public String color = "";

    public Card(JsonObject cardData){
        svgName = getSvgName(cardData.get("svgName").getAsString(), cardData.get("color").getAsString());
    }

    private String getSvgName(String svgName, String color){
        if(svgName == "ww" || svgName == "wd"){
            if(color == "none"){
                return svgName;
            }else{
                return svgName + "_" + color;
            }

        }else{
            return svgName;
        }
    }
}
