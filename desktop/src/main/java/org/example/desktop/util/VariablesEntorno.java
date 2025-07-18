package org.example.desktop.util;

import io.github.cdimascio.dotenv.Dotenv;

public class VariablesEntorno {
    private static final Dotenv dotenv = Dotenv.load();
//    private static final Dotenv dotenv = Dotenv.load();

//    public static String getHost(){
//        return dotenv.get("DB_HOST");
//    }
//
//    public static String getPort(){
//        return dotenv.get("DB_PORT");
//    }
//
//    public static String getDatabase(){
//        return dotenv.get("DB_DATABASE");
//    }
//
//    public static String getUser(){
//        return dotenv.get("DB_USER");
//    }
//
//    public static String getPassword(){
//        return dotenv.get("DB_PASSWORD");
//    }
//
//    public static String getJWTSecret(){
//        return dotenv.get("JWT_SECRET");
//    }
//
//    public static String getJWTExpiration(){
//        return dotenv.get("JWT_EXPIRATION");
//    }

    public static String getServerHost(){
//        return dotenv.get("SERVER_HOST");
        return "https://siaj-inventarios-production-b4ef.up.railway.app";
    }

    //poner https://siaj-inventarios-production-b4ef.up.railway.app/https://siaj-inventarios-production-b4ef.up.railway.app

//    public static String getServerPort(){
//        return "";
//    }

    public static String getServerURL(){
        return getServerHost();
    }

    public static String getCUIT(){
        return dotenv.get("CUIT");
    }
    public static String getKeyPath(){
        return dotenv.get("PATH_KEY");
    }
    public static String getCrtPath(){
        return dotenv.get("PATH_CRT");
    }
    public static String getTA(){
        return dotenv.get("TA_SECRET_PATH");
    }
}