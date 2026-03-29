package com.ornek;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class RegistrationController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField occupationField;
    @FXML
    private ComboBox<String> securityQuestionComboBox;
    @FXML
    private TextField securityAnswerField;
    @FXML
    private Label messageLabel;

    @FXML
    public void handleRegister(ActionEvent event) {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String ageStr = ageField.getText();
        String gender = genderComboBox.getValue();
        String occupation = occupationField.getText();
        String secQuestion = securityQuestionComboBox.getValue();
        String secAnswer = securityAnswerField.getText();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || gender == null
                || secQuestion == null
                || secAnswer.isEmpty()) {
            messageLabel.setText("Lütfen tüm zorunlu alanları doldurun!");
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            messageLabel.setText("Geçerli bir yaş giriniz!");
            return;
        }

        boolean success = DatabaseHelper.registerUser(fullName, username, password, email, age, gender, secQuestion,
                secAnswer,
                occupation);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Başarılı");
            alert.setHeaderText(null);
            alert.setContentText("Kayıt başarıyla tamamlandı! Giriş yapabilirsiniz.");
            alert.showAndWait();
            handleGoBackToLogin(event);
        } else {
            messageLabel.setText("Kayıt başarısız! Kullanıcı adı alınmış olabilir.");
        }
    }

    @FXML
    public void handleGoBackToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/ornek/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("PsikoAI - Giriş");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
