package com.ornek;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends StackPane {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
        initializeUI();
    }

    private void initializeUI() {
        // Ana Konteyner (StackPane) Ayarları
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;");

        // Dış VBox (Ortalamak için)
        VBox outerVBox = new VBox(25);
        outerVBox.setAlignment(Pos.CENTER);
        outerVBox.setFillWidth(false);

        // İç Kart VBox (Card Design)
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(30));
        cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 20, 0, 0, 5);");
        // Genişliği sınırla veya esnek yap
        cardBox.setPrefWidth(400);

        // Başlıklar
        Label titleLabel = new Label("PsikoAI");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1c1e21"));

        Label subtitleLabel = new Label("Lütfen hesabınızla giriş yapın");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#606770"));

        // Kullanıcı Adı Alanı
        VBox usernameBox = new VBox(8);
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        usernameBox.setMaxWidth(Double.MAX_VALUE);
        Label userLabel = new Label("Kullanıcı Adı:");
        userLabel.setStyle("-fx-text-fill: #1c1e21;");
        usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı adınızı giriniz");
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dddfe2; -fx-border-width: 1; -fx-background-color: #f5f6f7; -fx-padding: 10; -fx-font-size: 14px;");
        addFocusStyle(usernameField);
        usernameBox.getChildren().addAll(userLabel, usernameField);

        // Şifre Alanı
        VBox passwordBox = new VBox(8);
        passwordBox.setAlignment(Pos.CENTER_LEFT);
        passwordBox.setMaxWidth(Double.MAX_VALUE);
        Label passLabel = new Label("Şifre:");
        passLabel.setStyle("-fx-text-fill: #1c1e21;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Şifrenizi giriniz");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dddfe2; -fx-border-width: 1; -fx-background-color: #f5f6f7; -fx-padding: 10; -fx-font-size: 14px;");
        addFocusStyle(passwordField);
        passwordBox.getChildren().addAll(passLabel, passwordField);

        // Giriş Butonu
        Button loginButton = new Button("Giriş Yap");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #166fe5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #1877f2; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;"));
        loginButton.setOnAction(e -> handleLogin());

        // Alt Linkler
        HBox linksBox = new HBox(10);
        linksBox.setAlignment(Pos.CENTER);
        Hyperlink registerLink = new Hyperlink("Kayıt Ol");
        registerLink.setOnAction(e -> handleGoToRegistration());
        Label separator = new Label("|");
        separator.setTextFill(Color.web("#606770"));
        Hyperlink forgotPasswordLink = new Hyperlink("Şifremi Unuttum");
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        linksBox.getChildren().addAll(registerLink, separator, forgotPasswordLink);

        // Mesaj Etiketi
        messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#e74c3c"));

        // Elemanları Karta Ekle
        cardBox.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                usernameBox,
                passwordBox,
                loginButton,
                linksBox,
                messageLabel
        );

        // Kartı Dış VBox'a, Onu da StackPane'e ekle
        outerVBox.getChildren().add(cardBox);
        this.getChildren().add(outerVBox);
    }
    
    private void addFocusStyle(TextField field) {
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #1877f2; -fx-border-width: 1; -fx-background-color: white; -fx-padding: 10; -fx-font-size: 14px;");
            } else {
                field.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dddfe2; -fx-border-width: 1; -fx-background-color: #f5f6f7; -fx-padding: 10; -fx-font-size: 14px;");
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (DatabaseHelper.validateLogin(username, password)) {
            try {
                // Giriş başarılı, şimdilik test sayfası FXML'ine geri dönüyoruz 
                // Diğer sayfalar dönüştürüldükçe burası da güncellenecek
                javafx.scene.Parent testPageRoot = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/ornek/test_page.fxml"));
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
        }
    }

    private void handleGoToRegistration() {
        try {
            // Şimdilik FXML yüklenecek, daha sonra RegistrationView ile değişecek
            javafx.scene.Parent registrationRoot = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/ornek/registration.fxml"));
            stage.setScene(new Scene(registrationRoot));
            stage.setTitle("PsikoAI - Yeni Kayıt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Şifre Sıfırlama");
        alert.setHeaderText(null);
        alert.setContentText("Şifre sıfırlama özelliği yakında eklenecek. Lütfen yöneticinizle iletişime geçin.");
        alert.showAndWait();
    }
}
