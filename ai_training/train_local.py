from unsloth import FastLanguageModel
import torch
from datasets import load_dataset
from trl import SFTTrainer
from transformers import TrainingArguments

# 1. Modeli Yükle (4-bit modunda, RAM dostu)
model, tokenizer = FastLanguageModel.from_pretrained(
    model_name = "unsloth/Qwen2.5-7B-bnb-4bit",
    max_seq_length = 2048,
    load_in_4bit = True,
)

# 2. LoRA Ayarları (Ekran kartına yüklenmesi için)
model = FastLanguageModel.get_peft_model(
    model,
    r = 16,
    target_modules = ["q_proj", "k_proj", "v_proj", "o_proj",
                      "gate_proj", "up_proj", "down_proj",],
    lora_alpha = 16,
    lora_dropout = 0,
    bias = "none",
)

# 3. Veri Setini Hazırla
dataset = load_dataset("json", data_files="scl90_training_data.jsonl", split="train")

def formatting_prompts_func(examples):
    instructions = examples["instruction"]
    inputs       = examples["input"]
    outputs      = examples["output"]
    texts = []
    for instruction, input, output in zip(instructions, inputs, outputs):
        text = f"### Instruction:\n{instruction}\n\n### Input:\n{input}\n\n### Response:\n{output}"
        texts.append(text)
    return { "text" : texts, }

dataset = dataset.map(formatting_prompts_func, batched = True)

# 4. Eğitimi Başlat
trainer = SFTTrainer(
    model = model,
    train_dataset = dataset,
    dataset_text_field = "text",
    max_seq_length = 2048,
    args = TrainingArguments(
        per_device_train_batch_size = 2,
        gradient_accumulation_steps = 4,
        max_steps = 100, # Ayarlandığı kadar adım atar
        learning_rate = 2e-4,
        fp16 = not torch.cuda.is_bf16_supported(),
        bf16 = torch.cuda.is_bf16_supported(),
        logging_steps = 1,
        output_dir = "outputs",
        save_strategy = "no",
    ),
)

print("Eğitim başlıyor...")
trainer.train()

# 5. Modeli Ollama için Kaydet (GGUF formatı)
print("Model GGUF formatına dönüştürülüyor (Bu işlem biraz vakit alabilir)...")
model.save_pretrained_gguf("psiko_model_gguf", tokenizer, quantization_method = "q4_k_m")

print("Eğitim ve kayıt tamamlandı! 'psiko_model_gguf' klasöründeki dosyayı Ollama'ya çekebilirsin.")
