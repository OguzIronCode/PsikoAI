import json
import random

# SCL-90 Alt Boyutlar ve Klinik Karşılıkları
dimensions = [
    "Somatizasyon", "Obsesif-Kompulsif", "Kişilerarası Duyarlılık", 
    "Depresyon", "Anksiyete", "Öfke-Hostilite", "Fobik Anksiyete", 
    "Paranoid Düşünce", "Psikotizm", "Ek Maddeler"
]

def generate_sample():
    age = random.randint(18, 65)
    gender = random.choice(["Erkek", "Kadın"])
    
    # Rastgele ham puanlar üret (0.00 - 4.00)
    scores = {d: round(random.uniform(0, 3.5), 2) for d in dimensions}
    gsi = round(sum(scores.values()) / len(dimensions), 2)
    
    # Kullanıcı notu havuzu (Şiddete göre)
    if gsi > 2.0:
        notes = ["Kendimi çok kötü hissediyorum, yataktan çıkmak istemiyorum.", "Her şey üzerime geliyor gibi.", "Kimse beni anlamıyor, çok öfkeliyim."]
    elif gsi > 1.0:
        notes = ["Biraz gerginim bu aralar.", "Uyku sorunlarım var.", "İnsanlarla iletişim kurmak yorucu geliyor."]
    else:
        notes = ["Genel olarak iyiyim.", "Sadece biraz yorgunum.", "Her şey normal gidiyor."]
    
    note = random.choice(notes)
    
    # Profesyonel Analiz Oluşturma (Logic)
    high_scales = [k for k, v in scores.items() if v > 1.5]
    analysis = f"Klinik Değerlendirme ({age} yaşında, {gender} danışan): Bireyin GSI puanı ({gsi}) "
    analysis += "kritik eşik değerinin üzerindedir ve profesyonel destek önerilir. " if gsi >= 1.0 else "normal sınırlar içerisindedir. "
    
    if high_scales:
        analysis += f"Özellikle {', '.join(high_scales)} boyutlarındaki yükselme, "
        if "Depresyon" in high_scales: analysis += "belirgin bir duygu durum çöküntüsüne; "
        if "Anksiyete" in high_scales: analysis += "yoğun kaygı ve irritabiliteye; "
        analysis += "işaret etmektedir. "
    
    analysis += f"Kullanıcı notundaki '{note}' ifadesi, test skorlarıyla klinik olarak tutarlıdır."

    # Dataset Formatı
    return {
        "instruction": "Aşağıdaki demografik bilgiler, SCL-90 skorları ve kullanıcı notunu analiz ederek profesyonel bir psikolojik değerlendirme raporu yaz.",
        "input": f"Demografi: {age} yaş, {gender} | Skorlar: {json.dumps(scores)} | GSI: {gsi} | Kullanıcı Notu: {note}",
        "output": analysis
    }

# 500 adet örnek veri üret
dataset = [generate_sample() for _ in range(500)]

with open("scl90_training_data.jsonl", "w", encoding="utf-8") as f:
    for entry in dataset:
        f.write(json.dumps(entry, ensure_ascii=False) + "\n")

print("Eğitim veri seti başarıyla oluşturuldu: scl90_training_data.jsonl")
