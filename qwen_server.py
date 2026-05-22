"""
Qwen 本地推理服务器 - 为 Spring Boot 提供 AI 对话接口
启动方式: python qwen_server.py
端口: 5050
"""
from flask import Flask, request, jsonify
from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

app = Flask(__name__)

MODEL_PATH = r"D:\qwen\qwen\Qwen2___5-0___5B-Instruct"

print("正在加载 Qwen 模型，请稍候...")
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH, trust_remote_code=True)
model = AutoModelForCausalLM.from_pretrained(
    MODEL_PATH,
    torch_dtype=torch.float32,
    device_map="cpu",
    trust_remote_code=True
)
model.eval()
print("模型加载完成！")


@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()
    message = data.get("message", "")

    if not message:
        return jsonify({"error": "message 不能为空"}), 400

    messages = [
        {"role": "system", "content": "你是一个有帮助的AI助手，请简洁地回答用户的问题。"},
        {"role": "user", "content": message}
    ]

    text = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )

    inputs = tokenizer([text], return_tensors="pt").to("cpu")

    with torch.no_grad():
        outputs = model.generate(
            **inputs,
            max_new_tokens=512,
            do_sample=True,
            top_p=0.7,
            temperature=0.7,
            pad_token_id=tokenizer.eos_token_id
        )

    response = tokenizer.decode(
        outputs[0][len(inputs.input_ids[0]):],
        skip_special_tokens=True
    )

    return jsonify({"answer": response.strip()})


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


if __name__ == "__main__":
    print("Qwen 推理服务器启动在 http://127.0.0.1:5050")
    app.run(host="127.0.0.1", port=5050, debug=False)