package com.ornek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Veritabanını hazırla
        DatabaseHelper.initializeDatabase();

        try {
            // FXML kullanmadan Programatik Login Arayüzünü yüklüyoruz
            LoginView loginRoot = new LoginView(primaryStage);
            Scene scene = new Scene(loginRoot, 800, 600);

            primaryStage.setTitle("PsikoAI - Giriş Yap");
            primaryStage.setScene(scene);

            // Pencereyi ekranın ortasında başlat
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(500);

            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Uygulama başlatılırken hata oluştu:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
