package com.ornek;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    // FXML dosyasındaki fx:id değerleri ile aynı isimde olmalı!
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * "Giriş Yap" butonuna tıklandığında çalışacak metod.
     * FXML dosyasındaki onAction="#handleLogin" ile bağlanır.
     */
    @FXML
    public void handleLogin(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (DatabaseHelper.validateLogin(username, password)) {
            try {
                // Giriş başarılı, Test Sayfasına geç
                Parent testPageRoot = FXMLLoader.load(getClass().getResource("/com/ornek/test_page.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(testPageRoot);
                stage.setScene(scene);
                stage.setTitle("PsikoAI - Kişilik Testi");
                stage.centerOnScreen();
            } catch (Exception e) {
                messageLabel.setText("Sayfa yüklenirken hata oluştu!");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Hatalı kullanıcı adı veya şifre!");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void handleGoToRegistration(javafx.event.ActionEvent event) {
        try {
            Parent registrationRoot = FXMLLoader.load(getClass().getResource("/com/ornek/registration.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registrationRoot));
            stage.setTitle("PsikoAI - Yeni Kayıt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleForgotPassword(javafx.event.ActionEvent event) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Şifre Sıfırlama");
        alert.setHeaderText(null);
        alert.setContentText("Şifre sıfırlama özelliği yakında eklenecek. Lütfen yöneticinizle iletişime geçin.");
        alert.showAndWait();
    }
}
