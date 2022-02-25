package com.far.paynetsdkexample.Global;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.agilisa.devices.global.Global;
import com.agilisa.devices.models.Configuration;
import com.agilisa.devices.models.FiscalControlTokenResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Functions {


    public static  String getPreference(Context context,String key){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Globals.PREFERENCE_PAYNET_SDK_EXAMPLE_KEY, Context.MODE_PRIVATE);
        String value = sharedPref.getString(key,"");
        return value;
    }
    public static void saveControlFiscalResponse(Context context, FiscalControlTokenResponse fiscalControlTokenResponse){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Globals.PREFERENCE_PAYNET_SDK_EXAMPLE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Globals.PREFERENCE_FISCAL_CONTROL_TOKEN_RESPONSE, Functions.parseToJson(fiscalControlTokenResponse));
        editor.commit();
    }

    public static FiscalControlTokenResponse getFiscalControlToken(Context context){
        try{
            return new Gson().fromJson(Functions.getPreference(context, Globals.PREFERENCE_FISCAL_CONTROL_TOKEN_RESPONSE), FiscalControlTokenResponse.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String parseToJson(Object obj){
        if(obj == null){
            return null;
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //Gson gson = new Gson();
        String jsonObject =gson.toJson(obj);
        return jsonObject;
    }
}
