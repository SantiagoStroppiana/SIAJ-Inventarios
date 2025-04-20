package org.example;

import io.javalin.Javalin;

public class Pepe {


    public Pepe(){

    }

    public void prueba(Javalin app){

        app.get("/mensaje", ctx ->  {
            ctx.result("HOLA SOY PEPE ESTOY EN EL BACK");
        });

    }
}
