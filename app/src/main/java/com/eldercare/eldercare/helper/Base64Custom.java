package com.eldercare.eldercare.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto){
        //Codifica a string para base64
        return Base64.encodeToString(texto.getBytes(), Base64.NO_WRAP).replaceAll(
                "( \\n|\\r)", "");
    }

    public static String descodificarBase64(String texto){
        return new String(Base64.decode(texto, Base64.NO_WRAP));
    }

}
