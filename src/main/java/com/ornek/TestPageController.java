package com.ornek;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestPageController {

    @FXML
    private Label questionLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ToggleGroup answerGroup;
    @FXML
    private VBox startContainer;
    @FXML
    private VBox questionContainer;
    @FXML
    private VBox finishContainer;
    @FXML
    private RadioButton opt0, opt1, opt2, opt3, opt4;
    @FXML
    private Button skipButton;

    public void initialize() {
        // "Testi Geç" butonu her zaman aktif olsun
        skipButton.setVisible(true);
        skipButton.setManaged(true);
    }

    private int currentQuestionIndex = 0;
    private final List<Integer> userAnswers = new ArrayList<>();

    private final List<String> questions = Arrays.asList(
            "Baş ağrısı",
            "Sinirlilik ya da içinin titremesi",
            "Zihinden atamadığınız tekrarlayan, hoşa gitmeyen düşünceler",
            "Baygınlık ya da baş dönmesi",
            "Cinsel arzu ve ilginin kaybı",
            "Başkaları tarafından eleştirilme duygusu",
            "Herhangi bir kimsenin düşüncelerinizi kontrol edebileceği fikri",
            "Sorunlarınızdan pek çoğu için başkalarının suçlanması gerektiği duygusu",
            "Olayları anımsamada güçlük",
            "Dikkatsizlik ya da sakarlıkla ilgili düşünceler",
            "Kolayca gücenme, rahatsız olma hissi",
            "Göğüs ya da kalp bölgesinde ağrılar",
            "Caddelerde veya açık alanlarda korku hissi",
            "Enerjinizde azalma veya yavaşlama hali",
            "Yaşamınızın sonlanması düşünceleri",
            "Başka kişilerin duymadıkları sesleri duyma",
            "Titreme",
            "Çoğu kişiye güvenilmemesi gerektiği hissi",
            "İştah azalması",
            "Kolayca ağlama",
            "Karşı cinsten kişilerle utangaçlık ve rahatsızlık hissi",
            "Tuzağa düşürülmüş veya yakalanmış olma hissi",
            "Bir neden olmaksızın aniden korkuya kapılma",
            "Kontrol edilemeyen öfke patlamaları",
            "Evden dışarı yalnız çıkma korkusu",
            "Olanlar için kendisini suçlama",
            "Belin alt kısmında ağrılar",
            "İşlerin yapılmasında erteleme duygusu",
            "Yalnızlık hissi",
            "Karamsarlık hissi",
            "Her şey için çok fazla endişe duyma",
            "Her şeye karşı ilgisizlik hali",
            "Korku hissi",
            "Duygularınızın kolayca incitilebilmesi hali",
            "Diğer insanların sizin özel düşüncelerinizi bilmesi",
            "Başkalarının sizi anlamadığı veya hissedemeyeceği duygusu",
            "Başkalarının sizi sevmediği ya da dostça olmayan davranışlar gösterdiği hissi",
            "İşlerin doğru yapıldığından emin olmak için çok yavaş yapmak",
            "Kalbin çok hızlı çarpması",
            "Bulantı ve midede rahatsızlık hissi",
            "Kendini başkalarından aşağı görme",
            "Adale (kas) ağrıları",
            "Başkalarının sizi gözlediği veya hakkınızda konuştuğu hissi",
            "Uykuya dalmada güçlük",
            "Yaptığınız işleri bir ya da birkaç kez kontrol etme",
            "Karar vermede güçlük",
            "Otobüs, tren, metro gibi araçlarla yolculuk etme korkusu",
            "Nefes almada güçlük",
            "Soğuk veya sıcak basması",
            "Sizi korkutan belirli uğraş, yer veya nesnelerden kaçınma durumu",
            "Hiç bir şey düşünmeme hali",
            "Bedeninizin bazı kısımlarında uyuşma, karıncalanma olması",
            "Boğazınıza bir yumru takınmış hissi",
            "Gelecek konusunda ümitsizlik",
            "Düşüncelerinizi bir konuya yoğunlaştırmada güçlük",
            "Bedeninizin çeşitli kısımlarında zayıflık hissi",
            "Gerginlik veya coşku hissi",
            "Kol ve bacaklarda ağırlık hissi",
            "Ölüm ya da ölme düşünceleri",
            "Aşırı yemek yeme",
            "İnsanlar size baktığı veya hakkınızda konuştuğu zaman rahatsızlık duyma",
            "Size ait olmayan düşüncelere sahip olma",
            "Bir başkasına vurmak, zarar vermek, yaralamak dürtülerinin olması",
            "Sabahın erken saatlerinde uyanma",
            "Yıkanma, sayma, dokunma, gibi bazı hareketleri yineleme hali",
            "Uykuda huzursuzluk, rahat uyuyamama",
            "Bazı şeyleri kırıp dökme hissi",
            "Başkalarının paylaşıp kabul etmediği inanç ve düşüncelerin olması",
            "Başkalarının yanında kendini çok sıkılgan hissetme",
            "Çarşı, sinema gibi kalabalık yerlerde rahatsızlık hissi",
            "Her şeyin bir yük gibi görünmesi",
            "Dehşet ve panik nöbetleri",
            "Toplum içinde yer, içerken huzursuzluk hissi",
            "Sık sık tartışmaya girme",
            "Yalnız bırakıldığınızda sinirlilik hali",
            "Başkalarının sizi başarılarınız için yeterince takdir etmediği duygusu",
            "Başkalarıyla birlikte olunan durumlarda bile yalnızlık hissetme",
            "Yerinizde duramayacak ölçüde rahatsızlık hissetme",
            "Değersizlik duygusu",
            "Size kötü bir şey olacakmış hissi",
            "Bağırma ya da eşyaları fırlatma",
            "Topluluk içinde bayılacağınız korkusu",
            "Eğer izin verirseniz insanların sizi sömüreceği duygusu",
            "Cinsiyet konusunda sizi çok rahatsız eden düşüncelerin olması",
            "Günahlarınızdan dolayı cezalandırılmanız gerektiği düşüncesi",
            "Korkutu türden düşünce ve hayaller",
            "Bedeninizde ciddi bir rahatsızlık olduğu düşüncesi",
            "Başka bir kişiye karşı asla yakınlık duymama",
            "Suçluluk duygusu",
            "Aklınızda bir bozukluğun olduğu düşüncesi");

    @FXML
    public void handleStartTest() {
        startContainer.setVisible(false);
        startContainer.setManaged(false);
        questionContainer.setVisible(true);
        questionContainer.setManaged(true);
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            questionLabel.setText(questions.get(currentQuestionIndex));
            progressLabel.setText("Soru " + (currentQuestionIndex + 1) + " / " + questions.size());
            progressBar.setProgress((double) currentQuestionIndex / questions.size());
            answerGroup.selectToggle(null); // Seçimi temizle
        } else {
            finishTest();
        }
    }

    @FXML
    public void handleNext() {
        RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen bir seçenek belirleyin.");
            alert.showAndWait();
            return;
        }

        // Seçilen değerin sonundaki rakamı al (0, 1, 2, 3, 4)
        String valueStr = selected.getId().substring(3); // opt0 -> 0
        userAnswers.add(Integer.parseInt(valueStr));

        currentQuestionIndex++;
        showQuestion();
    }

    private void finishTest() {
        questionContainer.setVisible(false);
        questionContainer.setManaged(false);
        finishContainer.setVisible(true);
        finishContainer.setManaged(true);

        // Sonuçları hesapla
        Map<String, Double> results = SCL90Calculator.calculate(userAnswers);
        double gsi = results != null ? results.get("GSI") : 0.0;

        // Sonuçları string olarak birleştir (JSON veya virgülle ayrılmış)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userAnswers.size(); i++) {
            sb.append(userAnswers.get(i));
            if (i < userAnswers.size() - 1)
                sb.append(",");
        }

        // Veritabanına kaydet
        DatabaseHelper.saveTestResult("SCL-90", sb.toString(), gsi);
    }

    @FXML
    public void goToHomePage(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/com/ornek/home_page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("PsikoAI - Anasayfa");
            stage.setScene(new Scene(homeRoot, 1000, 700));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
