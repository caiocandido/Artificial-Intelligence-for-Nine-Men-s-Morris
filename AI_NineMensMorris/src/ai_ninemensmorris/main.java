/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_ninemensmorris;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ai_ninemensmorris.GUI;
import javafx.stage.Stage;
import javafx.application.Application;

/**
 *
 * @author Denis
 */
public class main extends Application {
    
    // Método que é executado quando se dá launch na main
    @Override
    public void start(Stage primaryStage) {
        GUI tela = new GUI();
        tela.setUpIntro(primaryStage); // Se prepara a cena de jogo
        tela.setUpPlay(); // Se prepara a cena de entrada do jogo. Se passa o stage pois ele será usado para se passar para a tela de jogo
        primaryStage.setTitle("Nine Men's Morris Project"); // Nome do título da aba que é mostrada
        primaryStage.setScene(tela.introScene); // A primeira cena a ser mostrada é a da tela inicial
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
