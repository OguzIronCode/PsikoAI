package com.ornek;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.awt.Desktop;
import java.net.URI;

public class CrisisScreenController {

    @FXML private Button contactExpertBtn;
    @FXML private TextField contactNameField;
    @FXML private TextField contactPhoneField;
    @FXML private Label exerciseGuideLabel;

    private Timeline exerciseTimeline;

    @FXML
    public void initialize() {
        contactExpertBtn.setOnAction(e -> openSupportLink());
        
        // Load existing emergency contact
        String[] contact = DatabaseHelper.getEmergencyContact();
        if (contact[0] != null) contactNameField.setText(contact[0]);
        if (contact[1] != null) contactPhoneField.setText(contact[1]);
    }

    private void openSupportLink() {
        browse("https://meet.google.com/"); // Gerçek destek ortamı
    }

    @FXML
    public void call112() { browse("tel:112"); }
    
    @FXML
    public void call155() { browse("tel:155"); }
    
    @FXML
    public void call182() { browse("tel:182"); }

    @FXML
    public void openMap() {
        browse("https://www.google.com/maps/search/psikiyatri+kriz+merkezi");
    }

    @FXML
    public void saveContact() {
        String name = contactNameField.getText().trim();
        String phone = contactPhoneField.getText().trim();
        if (!name.isEmpty() && !phone.isEmpty()) {
            DatabaseHelper.saveEmergencyContact(name, phone);
        }
    }

    @FXML
    public void sendWhatsAppMessage() {
        String phone = contactPhoneField.getText().trim();
        if (phone.isEmpty()) {
            exerciseGuideLabel.setText("Lütfen önce bir telefon numarası kaydedin.");
            return;
        }
        // Numaranın başındaki 0'ı atma ve +90 ekleme (basit kontrol)
        if (phone.startsWith("0")) phone = phone.substring(1);
        if (!phone.startsWith("90")) phone = "90" + phone;
        
        String message = "Şu an yardıma ihtiyacım var, zor bir an yaşıyorum. Lütfen benimle en kısa sürede iletişime geç.";
        String url = "https://wa.me/" + phone + "?text=" + message.replace(" ", "%20");
        browse(url);
    }

    @FXML
    public void startBreathing() {
        stopExercise();
        exerciseGuideLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        exerciseTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> exerciseGuideLabel.setText("Nefes Al... (4 sn)")),
            new KeyFrame(Duration.seconds(4), e -> exerciseGuideLabel.setText("Tut... (7 sn)")),
            new KeyFrame(Duration.seconds(11), e -> exerciseGuideLabel.setText("Nefes Ver... (8 sn)"))
        );
        exerciseTimeline.setCycleCount(Timeline.INDEFINITE);
        exerciseTimeline.play();
    }

    @FXML
    public void startGrounding() {
        stopExercise();
        exerciseGuideLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        exerciseTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> exerciseGuideLabel.setText("Etrafında GÖRDÜĞÜN 5 şeyi içinden say.")),
            new KeyFrame(Duration.seconds(8), e -> exerciseGuideLabel.setText("Dokunabildiğin 4 şeyi HİSSET.")),
            new KeyFrame(Duration.seconds(16), e -> exerciseGuideLabel.setText("Duyabildiğin 3 SESİ dinle.")),
            new KeyFrame(Duration.seconds(24), e -> exerciseGuideLabel.setText("Alabildiğin 2 KOKUYU fark et.")),
            new KeyFrame(Duration.seconds(32), e -> exerciseGuideLabel.setText("Ağzındaki 1 TADI fark et."))
        );
        exerciseTimeline.setCycleCount(1);
        exerciseTimeline.play();
    }

    private void stopExercise() {
        if (exerciseTimeline != null) {
            exerciseTimeline.stop();
        }
    }

    @FXML
    public void closeCrisisScreen(ActionEvent event) {
        stopExercise();
        // Since CrisisScreen is embedded in HomeController's StackPane, 
        // we can hide its parent container or fire an event.
        // A simple hack is to get the parent and set it to invisible.
        Node source = (Node) event.getSource();
        Node root = source.getScene().lookup("#crisisLayer");
        if (root != null) {
            // Trigger HomeController's hide method via reflection or just hide it
            root.setVisible(false);
            root.setManaged(false);
        } else {
            // Fallback: hide this stack pane directly
            source.getParent().getParent().setVisible(false);
        }
    }

    private void browse(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
