package com.ornek;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeController {

    @FXML
    private TableView<TestResult> resultsTable;
    @FXML
    private TableColumn<TestResult, String> dateColumn;
    @FXML
    private TableColumn<TestResult, String> testNameColumn;
    @FXML
    private TableColumn<TestResult, Double> gsiColumn;
    @FXML
    private TableColumn<TestResult, Void> actionColumn;
    @FXML
    private Label noDataLabel;
    @FXML
    private VBox mainContent;
    @FXML
    private Label userFullNameLabel;

    // Detay Paneli Bileşenleri
    @FXML
    private VBox detailPanel;
    @FXML
    private Label detailTitleLabel;
    @FXML
    private TextField somatizationField;
    @FXML
    private TextField anxietyField;
    @FXML
    private TextField obsessionField;
    @FXML
    private TextField depressionField;
    @FXML
    private TextField sensitivityField;
    @FXML
    private TextField psychoticismField;
    @FXML
    private TextField paranoidField;
    @FXML
    private TextField hostilityField;
    @FXML
    private TextField phobicField;
    @FXML
    private TextField additionalField;
    @FXML
    private TextField gsiField;
    @FXML
    private TextArea aiAnalysisResultArea;
    @FXML
    private Button aiAnalyzeBtn;

    private TestResult selectedTestForResult;

    // Duygu Paneli Bileşenleri
    @FXML
    private VBox feelingPanel;
    @FXML
    private TextArea feelingNotesArea;
    @FXML
    private Label charCountLabel;
    private String selectedFeeling = "";

    // Günlük Bölümü Bileşenleri
    @FXML
    private VBox journalContent;
    @FXML
    private DatePicker journalDatePicker;
    @FXML
    private TextArea journalTextArea;
    @FXML
    private Label journalStatusLabel;

    // Meditasyon Bölümü
    @FXML
    private VBox meditationContent;
    private Timeline meditationTimeline;
    private Circle meditationCircle;
    private Label meditationLabel;
    private Label timerLabel;
    private Label instructionLabel;
    private Button stopBtn;
    private long meditationStartTime;
    private int currentStepIndex = 0;
    private List<MeditationStep> currentSteps = new ArrayList<>();

    @FXML
    private StackPane crisisLayer;
    @FXML
    private Button emergencyBtn;
    private Parent crisisScreen;

    @FXML
    public void initialize() {
        refreshTable();
        setupActionColumn();

        // Günlük için bugünün tarihini seç
        journalDatePicker.setValue(LocalDate.now());
        loadJournalEntry();

        // Profil ismini ayarla
        String fullName = DatabaseHelper.getUserFullName(DatabaseHelper.getCurrentUserId());
        userFullNameLabel.setText(fullName);

        // Satır tıklama dinleyicisi (Çift tıklama ile detay)
        resultsTable.setRowFactory(tv -> {
            TableRow<TestResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showDetails(row.getItem());
                }
            });
            return row;
        });

        // Duygu notları kelime takibi
        feelingNotesArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String[] words = newVal.trim().split("\\s+");
                int count = newVal.trim().isEmpty() ? 0 : words.length;
                charCountLabel.setText(count + " / 500 kelime");
                charCountLabel.setStyle(count > 500 ? "-fx-text-fill: red;" : "-fx-text-fill: #606770;");
            }
        });

        // Kriz ekranı yüklemesi ve buton dinleyicisi
        try {
            crisisScreen = FXMLLoader.load(getClass().getResource("/com/ornek/CrisisScreen.fxml"));
            crisisScreen.setOpacity(0);
            crisisLayer.getChildren().add(crisisScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        emergencyBtn.setOnAction(e -> showCrisisScreen());
    }

    // --- Görünüm Değiştirme ---

    @FXML
    public void showHomeView() {
        stopMeditation();
        mainContent.setVisible(true);
        mainContent.setManaged(true);
        journalContent.setVisible(false);
        journalContent.setManaged(false);
        meditationContent.setVisible(false);
        meditationContent.setManaged(false);
        refreshTable();
    }

    @FXML
    public void showJournalView() {
        stopMeditation();
        mainContent.setVisible(false);
        mainContent.setManaged(false);
        journalContent.setVisible(true);
        journalContent.setManaged(true);
        meditationContent.setVisible(false);
        meditationContent.setManaged(false);
        loadJournalEntry();
    }

    @FXML
    public void showMeditationView() {
        mainContent.setVisible(false);
        mainContent.setManaged(false);
        journalContent.setVisible(false);
        journalContent.setManaged(false);
        meditationContent.setVisible(true);
        meditationContent.setManaged(true);

        if (meditationContent.getChildren().isEmpty()) {
            setupMeditationUI();
        }
        startMeditation("relax");
    }

    private void setupMeditationUI() {
        meditationContent.setAlignment(Pos.CENTER);
        meditationContent.setSpacing(25);
        meditationContent.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");

        Label title = new Label("ZİHİNSEL DİNGİNLİK MERKEZİ");
        title.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 26px; -fx-letter-spacing: 3px; -fx-font-weight: bold;");

        timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 18px;");

        instructionLabel = new Label("Başlamak için bir egzersiz seçin");
        instructionLabel.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 16px; -fx-font-style: italic;");
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(700);
        instructionLabel.setAlignment(Pos.CENTER);

        VBox header = new VBox(10, title, timerLabel, instructionLabel);
        header.setAlignment(Pos.CENTER);

        StackPane centerPane = new StackPane();
        meditationCircle = new Circle(120);
        meditationCircle.setFill(Color.TRANSPARENT);
        meditationCircle.setStroke(Color.web("#58a6ff"));
        meditationCircle.setStrokeWidth(2);
        meditationCircle.setOpacity(0.3);

        meditationLabel = new Label("HAZIR MISIN?");
        meditationLabel.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 28px; -fx-font-weight: 300; -fx-letter-spacing: 2px; -fx-text-alignment: center;");
        meditationLabel.setWrapText(true);
        meditationLabel.setMaxWidth(500);

        centerPane.getChildren().addAll(meditationCircle, meditationLabel);
        VBox.setVgrow(centerPane, Priority.ALWAYS);

        FlowPane controls = new FlowPane(15, 15);
        controls.setAlignment(Pos.CENTER);
        controls.setMaxWidth(900);
        
        controls.getChildren().addAll(
            createMeditationButton("4-7-8 Nefes (Yatıştırıcı)", "478"),
            createMeditationButton("5-4-3-2-1 Topraklanma", "54321"),
            createMeditationButton("Kas Gevşetme", "pmr"),
            createMeditationButton("Beden Taraması", "body_scan"),
            createMeditationButton("Öz-Şefkat Molası", "self_compassion"),
            createMeditationButton("Vokal Vagus Uyarımı", "vagus"),
            createMeditationButton("Kutu Nefesi", "box")
        );

        stopBtn = new Button("Egzersizi Durdur");
        stopBtn.setStyle("-fx-background-color: #f85149; -fx-text-fill: white; -fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        stopBtn.setVisible(false);
        stopBtn.setOnAction(e -> stopMeditation());

        meditationContent.getChildren().addAll(header, centerPane, controls, stopBtn);
    }

    private static class MeditationStep {
        String mainText;
        String instructionText;
        int durationSeconds;
        String actionType; // inhale, hold, exhale, step
        double scaleTarget;

        MeditationStep(String mainText, String instructionText, int durationSeconds, String actionType, double scaleTarget) {
            this.mainText = mainText;
            this.instructionText = instructionText;
            this.durationSeconds = durationSeconds;
            this.actionType = actionType;
            this.scaleTarget = scaleTarget;
        }
    }

    private Button createMeditationButton(String text, String mode) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-padding: 12 20; -fx-background-radius: 12; -fx-border-color: #30363d; -fx-border-radius: 12; -fx-cursor: hand; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #30363d; -fx-text-fill: white; -fx-padding: 12 20; -fx-background-radius: 12; -fx-border-color: #58a6ff; -fx-border-radius: 12; -fx-cursor: hand; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-padding: 12 20; -fx-background-radius: 12; -fx-border-color: #30363d; -fx-border-radius: 12; -fx-cursor: hand; -fx-font-size: 14px;"));
        btn.setOnAction(e -> startMeditation(mode));
        return btn;
    }

    private void startMeditation(String mode) {
        stopMeditation();
        currentSteps.clear();
        currentStepIndex = 0;
        meditationStartTime = System.currentTimeMillis();
        stopBtn.setVisible(true);

        switch (mode) {
            case "478":
                instructionLabel.setText("Vagus sinirini uyarmak için ideal.");
                for(int i=0; i<4; i++) {
                    currentSteps.add(new MeditationStep("NEFES AL", "Burnundan sessizce ve yavaşça...", 4, "inhale", 1.8));
                    currentSteps.add(new MeditationStep("TUT", "Bedenindeki durağanlığı fark et.", 7, "hold", 1.8));
                    currentSteps.add(new MeditationStep("NEFES VER", "Dudaklarını büzerek, yavaşça üfle.", 8, "exhale", 1.0));
                }
                break;
            case "54321":
                instructionLabel.setText("Zihni şimdiye demirlemek için (Anksiyete anları).");
                currentSteps.add(new MeditationStep("HAZIRLAN", "Şu an güvendesin. 3 kez derin nefes al.", 5, "step", 1.2));
                currentSteps.add(new MeditationStep("5 NESNE", "Gördüğün 5 nesnenin adını yüksek sesle söyle.", 8, "step", 1.2));
                currentSteps.add(new MeditationStep("4 HİS", "Bedenindeki 4 temas noktasını fark et.", 8, "step", 1.2));
                currentSteps.add(new MeditationStep("3 SES", "Duyabildiğin 3 farklı sesi bul.", 8, "step", 1.2));
                currentSteps.add(new MeditationStep("2 KOKU", "Burnundan nefes al ve 2 kokuyu fark et.", 6, "step", 1.2));
                currentSteps.add(new MeditationStep("1 TAD", "Son olarak ağzındaki 1 tada odaklan.", 5, "step", 1.2));
                currentSteps.add(new MeditationStep("TAMAMLANDI", "Şu an buradayım ve güvendeyim.", 5, "step", 1.0));
                break;
            case "pmr":
                instructionLabel.setText("Fiziksel gerginliği atmak için (Kas Gevşetme).");
                currentSteps.add(new MeditationStep("DERİN NEFES", "Ciğerlerini tamamen doldur.", 5, "inhale", 1.8));
                currentSteps.add(new MeditationStep("OMUZLARI KAS", "Omuzları kulaklara kaldır ve iyice sık!", 5, "step", 1.8));
                currentSteps.add(new MeditationStep("BIRAK VE RAHATLA", "Omuzları serbest bırak, rahatlamayı hisset.", 6, "exhale", 1.0));
                currentSteps.add(new MeditationStep("BACAKLARI KAS", "Kalça ve üst bacak kaslarını iyice sık.", 5, "step", 1.5));
                currentSteps.add(new MeditationStep("GEVŞET", "Bacaklarını serbest bırak. Rahatlamayı izle.", 6, "exhale", 1.0));
                break;
            case "body_scan":
                instructionLabel.setText("Uykuya geçiş ve derin gevşeme.");
                currentSteps.add(new MeditationStep("GÖZLERİ KAPAT", "Uzun ve derin nefesler al.", 6, "inhale", 1.5));
                currentSteps.add(new MeditationStep("AYAKLARINA ODAKLAN", "Ayaklarının yerle olan temasını hisset.", 8, "step", 1.5));
                currentSteps.add(new MeditationStep("YUKARI KAYDIR", "Bilekler, baldırlar ve dizlere doğru ilerle.", 8, "step", 1.5));
                currentSteps.add(new MeditationStep("KARIN BÖLGESİ", "Nefesin karnındaki hareketini izle.", 8, "step", 1.5));
                currentSteps.add(new MeditationStep("YUMUŞAMA", "Gergin bölgelere nefesini yönlendir.", 8, "step", 1.0));
                break;
            case "self_compassion":
                instructionLabel.setText("Zor duygularla başa çıkarken öz-şefkat.");
                currentSteps.add(new MeditationStep("ELİNİ KALBİNE KOY", "Stres hissettiğin yeri bul ve dokun.", 6, "step", 1.2));
                currentSteps.add(new MeditationStep("KABUL ET", "Nazikçe 'Bu bir stres anı' de.", 6, "step", 1.2));
                currentSteps.add(new MeditationStep("İNSANLIK HALİ", "Zorlanmak hayatın bir parçasıdır.", 6, "step", 1.2));
                currentSteps.add(new MeditationStep("ŞEFKAT İSTE", "Kendime karşı nazik olabilir miyim?", 8, "step", 1.0));
                break;
            case "vagus":
                instructionLabel.setText("Ses titreşimi ile anında sakinleşme.");
                for(int i=0; i<4; i++) {
                    currentSteps.add(new MeditationStep("DERİN NEFES", "Ciğerlerini tamamen doldur.", 4, "inhale", 1.8));
                    currentSteps.add(new MeditationStep("HIMMMMM", "Arı vızıltısı sesi çıkararak titreşime odaklan.", 8, "exhale", 1.0));
                }
                break;
            case "box":
                instructionLabel.setText("Konsantrasyon ve denge için.");
                for(int i=0; i<4; i++) {
                    currentSteps.add(new MeditationStep("NEFES AL", "", 4, "inhale", 1.8));
                    currentSteps.add(new MeditationStep("TUT", "", 4, "hold", 1.8));
                    currentSteps.add(new MeditationStep("NEFES VER", "", 4, "exhale", 1.0));
                    currentSteps.add(new MeditationStep("TUT", "", 4, "hold", 1.0));
                }
                break;
        }

        meditationTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> updateMeditationFrame()));
        meditationTimeline.setCycleCount(Animation.INDEFINITE);
        meditationTimeline.play();

        // İlk adımı seslendir
        if (!currentSteps.isEmpty()) {
            speak(currentSteps.get(0).mainText + ". " + currentSteps.get(0).instructionText);
        }
    }

    private void speak(String text) {
        if (text == null || text.isEmpty()) return;
        new Thread(() -> {
            try {
                // Windows PowerShell üzerinden seslendirme (UTF-8 desteği için -EncodedCommand kullanılabilir ama basitlik için şimdilik böyle)
                String script = "Add-Type -AssemblyName System.Speech; $s = New-Object System.Speech.Synthesis.SpeechSynthesizer; $s.Rate = -1; $s.Speak('" + text.replace("'", "''") + "')";
                ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", script);
                pb.start();
            } catch (Exception e) {
                // Sessizce geç
            }
        }).start();
    }

    private void updateMeditationFrame() {
        if (currentSteps.isEmpty() || currentStepIndex >= currentSteps.size()) {
            stopMeditation();
            meditationLabel.setText("TAMAMLANDI");
            instructionLabel.setText("Harikaydın. Hazır olduğunda devam edebilirsin.");
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - meditationStartTime;
        long totalDurationMillis = 0;
        for (int i = 0; i < currentStepIndex; i++) {
            totalDurationMillis += currentSteps.get(i).durationSeconds * 1000L;
        }

        long stepElapsedMillis = elapsedMillis - totalDurationMillis;
        MeditationStep currentStep = currentSteps.get(currentStepIndex);
        long stepDurationMillis = currentStep.durationSeconds * 1000L;

        if (stepElapsedMillis >= stepDurationMillis) {
            currentStepIndex++;
            if (currentStepIndex < currentSteps.size()) {
                MeditationStep nextStep = currentSteps.get(currentStepIndex);
                speak(nextStep.mainText + ". " + nextStep.instructionText);
                updateMeditationFrame();
            }
            return;
        }

        double p = (double) stepElapsedMillis / stepDurationMillis;
        String color;
        double scale;

        switch (currentStep.actionType) {
            case "inhale":
                color = "#58a6ff"; // Blue
                scale = 1.0 + p * (currentStep.scaleTarget - 1.0);
                break;
            case "exhale":
                color = "#3fb950"; // Green
                scale = currentStep.scaleTarget + (1.0 - p) * (1.8 - currentStep.scaleTarget);
                break;
            case "hold":
                color = "#d29922"; // Yellow
                scale = currentStep.scaleTarget;
                break;
            default: // step
                color = "#bc8cff"; // Purple
                scale = currentStep.scaleTarget;
                break;
        }

        meditationLabel.setText(currentStep.mainText);
        meditationLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 28px; -fx-font-weight: 300; -fx-letter-spacing: 2px;");
        instructionLabel.setText(currentStep.instructionText);
        meditationCircle.setScaleX(scale);
        meditationCircle.setScaleY(scale);
        meditationCircle.setStroke(Color.web(color));
        meditationCircle.setOpacity(0.3 + (p * 0.4));

        long timerSeconds = elapsedMillis / 1000;
        timerLabel.setText(String.format("%02d:%02d", timerSeconds / 60, timerSeconds % 60));
    }

    private void stopMeditation() {
        if (meditationTimeline != null) {
            meditationTimeline.stop();
        }
        if (stopBtn != null) {
            stopBtn.setVisible(false);
        }
        if (meditationCircle != null) {
            meditationCircle.setScaleX(1.0);
            meditationCircle.setScaleY(1.0);
            meditationCircle.setOpacity(0.3);
            meditationCircle.setStroke(Color.web("#58a6ff"));
        }
    }

    // --- Günlük İşlemleri ---

    @FXML
    public void loadJournalEntry() {
        LocalDate date = journalDatePicker.getValue();
        if (date != null) {
            String entry = DatabaseHelper.getJournalEntry(date.toString());
            journalTextArea.setText(entry);
            journalStatusLabel.setText(entry.isEmpty() ? "Bu tarih için kayıt yok." : "Kayıt yüklendi.");
        }
    }

    @FXML
    public void saveJournal() {
        LocalDate date = journalDatePicker.getValue();
        String content = journalTextArea.getText();

        if (date == null)
            return;

        if (DatabaseHelper.saveJournalEntry(date.toString(), content)) {
            journalStatusLabel.setText("Günlük başarıyla kaydedildi!");
            journalStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            journalStatusLabel.setText("Kaydetme sırasında bir hata oluştu.");
            journalStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    // --- Duygu Paneli İşlemleri ---

    @FXML
    public void showFeelingPanel() {
        mainContent.setOpacity(0.3);
        journalContent.setOpacity(0.3);
        feelingPanel.setVisible(true);
        feelingPanel.setManaged(true);
    }

    @FXML
    public void hideFeelingPanel() {
        feelingPanel.setVisible(false);
        feelingPanel.setManaged(false);
        mainContent.setOpacity(1.0);
        journalContent.setOpacity(1.0);
    }

    @FXML
    public void handleFeelingSelect(ActionEvent event) {
        Button btn = (Button) event.getSource();
        selectedFeeling = btn.getText();
        btn.getParent().getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof Button)
                node.getStyleClass().remove("feeling-btn-selected");
        });
        btn.getStyleClass().add("feeling-btn-selected");
    }

    @FXML
    public void saveFeeling() {
        if (selectedFeeling.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Lütfen bir duygu seçin.");
            alert.showAndWait();
            return;
        }
        if (DatabaseHelper.saveUserFeeling(selectedFeeling, feelingNotesArea.getText())) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Başarılı");
            success.setHeaderText("Duygunuz kaydedildi.");
            success.setContentText("Harika! Bu veriler analizinde yardımcı olacak.");
            success.showAndWait();
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Duygu kaydedilirken bir hata oluştu.");
            error.showAndWait();
        }

        hideFeelingPanel();
        feelingNotesArea.clear();
        selectedFeeling = "";
    }

    // --- Tablo ve Test İşlemleri ---

    @FXML
    public void refreshTable() {
        List<TestResult> results = DatabaseHelper.getUserTestResults();
        if (results.isEmpty()) {
            resultsTable.setVisible(false);
            resultsTable.setManaged(false);
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);
        } else {
            resultsTable.setVisible(true);
            resultsTable.setManaged(true);
            noDataLabel.setVisible(false);
            noDataLabel.setManaged(false);
            resultsTable.setItems(FXCollections.observableArrayList(results));
        }
    }

    private void setupActionColumn() {
        Callback<TableColumn<TestResult, Void>, TableCell<TestResult, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Sil");
            {
                btn.setStyle("-fx-background-color: #ff7675; -fx-text-fill: white; -fx-padding: 5 10;");
                btn.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void showDetails(TestResult result) {
        this.selectedTestForResult = result;
        detailTitleLabel.setText(result.getTestName() + " - " + result.getDate());

        // Önceki analizi gizle
        aiAnalysisResultArea.setVisible(false);
        aiAnalysisResultArea.setManaged(false);
        aiAnalyzeBtn.setDisable(false);
        aiAnalyzeBtn.setText("🤖 Yapay Zeka Analizi Oluştur");
        String raw = result.getRawAnswers();
        List<Integer> answers = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String p : raw.split(",")) {
                try {
                    answers.add(Integer.parseInt(p.trim()));
                } catch (NumberFormatException e) {
                }
            }
        }
        if (!answers.isEmpty()) {
            Map<String, Double> scores = SCL90Calculator.calculate(answers);
            if (scores != null) {
                somatizationField.setText(String.format("%.2f", scores.getOrDefault("Somatizasyon", 0.0)));
                anxietyField.setText(String.format("%.2f", scores.getOrDefault("Anksiyete", 0.0)));
                obsessionField.setText(String.format("%.2f", scores.getOrDefault("Obsesif-Kompulsif", 0.0)));
                depressionField.setText(String.format("%.2f", scores.getOrDefault("Depresyon", 0.0)));
                sensitivityField.setText(String.format("%.2f", scores.getOrDefault("Kişilerarası Duyarlılık", 0.0)));
                psychoticismField.setText(String.format("%.2f", scores.getOrDefault("Psikotizm", 0.0)));
                paranoidField.setText(String.format("%.2f", scores.getOrDefault("Paranoid Düşünce", 0.0)));
                hostilityField.setText(String.format("%.2f", scores.getOrDefault("Öfke-Hostilite", 0.0)));
                phobicField.setText(String.format("%.2f", scores.getOrDefault("Fobik Anksiyete", 0.0)));
                additionalField.setText(String.format("%.2f", scores.getOrDefault("Ek Maddeler", 0.0)));
                gsiField.setText(String.format("%.2f", scores.getOrDefault("GSI", 0.0)));
            }
        }
        mainContent.setOpacity(0.5);
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    @FXML
    public void hideDetails() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        mainContent.setOpacity(1.0);
    }

    private void handleDelete(TestResult result) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bu test sonucunu silmek istediğinize emin misiniz?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Testi Sil");
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (DatabaseHelper.deleteTestResult(result.getId()))
                refreshTable();
        }
    }

    @FXML
    public void handleNewTest(ActionEvent event) {
        try {
            Parent testRoot = FXMLLoader.load(getClass().getResource("/com/ornek/test_page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("PsikoAI - Kişilik Testi");
            stage.setScene(new Scene(testRoot, 1000, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            DatabaseHelper.setCurrentUserId(-1);
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/ornek/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("PsikoAI - Giriş");
            stage.setScene(new Scene(loginRoot, 1000, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAIAnalysis() {
        if (selectedTestForResult == null)
            return;

        aiAnalyzeBtn.setDisable(true);
        aiAnalyzeBtn.setText("⌛ Analiz Hazırlanıyor...");

        String raw = selectedTestForResult.getRawAnswers();
        List<Integer> answers = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String p : raw.split(",")) {
                try {
                    answers.add(Integer.parseInt(p.trim()));
                } catch (Exception e) {
                }
            }
        }

        Map<String, Double> scores = SCL90Calculator.calculate(answers);
        String latestFeeling = DatabaseHelper.getLatestFeeling();

        OllamaService.generateAnalysis(scores, latestFeeling).thenAccept(analysis -> {
            javafx.application.Platform.runLater(() -> {
                String finalText = analysis;
                boolean shouldTriggerCrisis = false;
                if (finalText != null && finalText.contains("[RISK=HIGH]")) {
                    shouldTriggerCrisis = true;
                    finalText = finalText.replace("[RISK=HIGH]", "").trim();
                }
                aiAnalysisResultArea.setText(finalText);
                aiAnalysisResultArea.setVisible(true);
                aiAnalysisResultArea.setManaged(true);
                aiAnalyzeBtn.setText("✅ Analiz Tamamlandı");
                
                if (shouldTriggerCrisis) {
                    showCrisisScreen();
                }
            });
        });
    }

    // --- Kriz Ekranı Fonksiyonları ---
    private void showCrisisScreen() {
        crisisLayer.setVisible(true);
        crisisLayer.setManaged(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), crisisScreen);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // Kriz ekranını kapatmak için (CrisisScreenController'dan çağrılabilir)
    public void hideCrisisScreen() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), crisisScreen);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            crisisLayer.setVisible(false);
            crisisLayer.setManaged(false);
        });
        fadeOut.play();
    }
}
