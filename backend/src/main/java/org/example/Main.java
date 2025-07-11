package org.example;

public class Main {
    public static void main(String[] args) {

        try{
            BackendServer.iniciar();
        }catch (Exception e){
            System.err.println(" Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }
}