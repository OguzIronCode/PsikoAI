package com.ornek;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "qwen2.5:7b"; // Senin bilgisayarındaki model
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static CompletableFuture<String> generateAnalysis(Map<String, Double> sclScores, String userFeeling) {
        // Prompt Hazırlama (Klinik standarta uygun)
        StringBuilder prompt = new StringBuilder();
        prompt.append(
                "Sen uzman bir klinik psikologsun. Aşağıdaki verileri analiz et ve profesyonel bir rapor oluştur:\n\n");
        prompt.append("SCL-90 Test Skorları (Ham Puanlar):\n");

        sclScores.forEach((key, value) -> {
            prompt.append("- ").append(key).append(": ").append(String.format("%.2f", value)).append("\n");
        });

        prompt.append("\nKullanıcının 'Nasıl Hissediyorsun?' Notu:\n");
        prompt.append("\"").append(userFeeling).append("\"\n\n");

        prompt.append("Analizinde şu kurallara uy:\n");
        prompt.append("1. GSI puanı 1.00 üzerindeyse klinik yardım gerekliliğini vurgula.\n");
        prompt.append("2. Skorlar ile kullanıcı notu arasındaki tutarlılığı/çelişkiyi açıkla.\n");
        prompt.append("3. Empatik ama profesyonel bir dil kullan.\n");
        prompt.append("4. Çözüm odaklı öneriler sun.\n");
        prompt.append("5. Eğer kullanıcının notunda veya skorlarında KENDİNE ZARAR VERME, İNTİHAR veya AĞIR KRİZ belirtisi görürsen, raporun EN BAŞINA tam olarak şu ifadeyi ekle: [RISK=HIGH]\n");

        // Request Body
        JsonObject json = new JsonObject();
        json.addProperty("model", MODEL_NAME);
        json.addProperty("prompt", prompt.toString());
        json.addProperty("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
                        return responseJson.get("response").getAsString();
                    } else {
                        return "Hata: Ollama modeline ulaşılamadı. (Status: " + response.statusCode() + ")";
                    }
                })
                .exceptionally(ex -> "Ollama Bağlantı Hatası: " + ex.getMessage());
    }
}
