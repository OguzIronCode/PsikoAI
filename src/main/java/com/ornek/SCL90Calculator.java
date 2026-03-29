package com.ornek;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SCL90Calculator {

    // SCL-90-R Alt Ölçek Tanımları (Soru Numaraları)
    private static final Map<String, int[]> SCALES_MAPPING = new HashMap<>();

    static {
        SCALES_MAPPING.put("Somatizasyon", new int[] { 1, 4, 12, 27, 40, 42, 48, 49, 52, 53, 56, 58 });
        SCALES_MAPPING.put("Obsesif-Kompulsif", new int[] { 3, 9, 10, 28, 38, 45, 46, 51, 55, 65 });
        SCALES_MAPPING.put("Kişilerarası Duyarlılık", new int[] { 6, 21, 34, 36, 37, 41, 61, 69, 73 });
        SCALES_MAPPING.put("Depresyon", new int[] { 5, 14, 15, 20, 22, 26, 29, 30, 31, 32, 54, 71, 79 });
        SCALES_MAPPING.put("Anksiyete", new int[] { 2, 17, 23, 33, 39, 57, 72, 78, 80, 86 });
        SCALES_MAPPING.put("Öfke-Hostilite", new int[] { 11, 24, 63, 67, 74, 81 });
        SCALES_MAPPING.put("Fobik Anksiyete", new int[] { 13, 25, 47, 50, 70, 75, 82 });
        SCALES_MAPPING.put("Paranoid Düşünce", new int[] { 8, 18, 43, 68, 76, 83 });
        SCALES_MAPPING.put("Psikotizm", new int[] { 7, 16, 35, 62, 77, 84, 85, 87, 88, 90 });
        SCALES_MAPPING.put("Ek Maddeler", new int[] { 19, 44, 59, 60, 64, 89 });
    }

    /**
     * SCL-90-R Hesaplama
     * 
     * @param userAnswers 0-4 arası cevaplardan oluşan liste (1. soru 0.
     *                    indekstedir)
     * @return Hesaplanan puanlar
     */
    public static Map<String, Double> calculate(List<Integer> userAnswers) {
        if (userAnswers == null || userAnswers.size() != 90) {
            System.err.println(
                    "Hata: Cevap sayısı 90 olmalıdır. Mevcut: " + (userAnswers != null ? userAnswers.size() : 0));
            return null;
        }

        Map<String, Double> results = new HashMap<>();
        double totalScore = 0;

        // 1. Alt Ölçekleri Hesapla
        for (Map.Entry<String, int[]> entry : SCALES_MAPPING.entrySet()) {
            double sum = 0;
            int[] indices = entry.getValue();
            for (int qNum : indices) {
                sum += userAnswers.get(qNum - 1); // İndeksleme için 1 çıkarıyoruz
            }
            results.put(entry.getKey(), Math.round((sum / indices.length) * 100.0) / 100.0);
        }

        // 2. GSI Hesapla
        for (int score : userAnswers) {
            totalScore += score;
        }
        results.put("GSI", Math.round((totalScore / 90.0) * 100.0) / 100.0);

        return results;
    }

    public static String interpretGSI(double gsi) {
        if (gsi <= 1.50)
            return "Normal";
        if (gsi <= 2.50)
            return "Araz Düzeyi Yüksek";
        return "Araz Düzeyi Çok Yüksek";
    }
}
