package org.example.desktop;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PepeControlador {

    @FXML
    private Label pepeLabel;

    @FXML
    public void obtenerMensajeDePepe(javafx.event.ActionEvent actionEvent) {

        try{

            URL url = new URL("http://localhost:7000/mensaje");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String mensaje = br.readLine();
            br.close();

            pepeLabel.setText(mensaje);

        }catch (Exception e){
            pepeLabel.setText("El texto no existe");
            e.printStackTrace();
        }

    }
}
