import os
import json

base = os.path.dirname(os.path.abspath(__file__))
sounds_json = {
    "megumin_explosion_chant": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion_chant",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion_chant", "stream": False}
        ]
    },
    "megumin_explosion_charging": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion_charging",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion_charging", "stream": False}
        ]
    },
    "megumin_explosion_ray": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion_ray",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion_ray", "stream": False}
        ]
    },
    "megumin_explosion_detonation": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion_detonation",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion_detonation", "stream": False}
        ]
    },
    "megumin_explosion_blast": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion_blast",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion_blast", "stream": False}
        ]
    },
    "megumin_explosion": {
        "category": "players",
        "subtitle": "subtitles.frieren_flight.megumin_explosion",
        "sounds": [
            {"name": "frieren_flight:mob/megumin_explosion", "stream": True}
        ]
    }
}

target_resource_dirs = [
    os.path.join(base, "common", "src", "main", "resources"),
    os.path.join(base, "src", "main", "resources"),
    os.path.join(base, "neoforge", "src", "main", "resources"),
    os.path.join(base, "forge", "src", "main", "resources"),
]

for res_dir in target_resource_dirs:
    sj_dir = os.path.join(res_dir, "assets", "frieren_flight")
    os.makedirs(sj_dir, exist_ok=True)
    sj_path = os.path.join(sj_dir, "sounds.json")
    with open(sj_path, "w", encoding="utf-8") as f:
        json.dump(sounds_json, f, indent=2, ensure_ascii=False)
    print(f"Wrote sounds.json to {sj_path}")

subtitles_en = {
    "subtitles.frieren_flight.megumin_explosion_chant": "Megumin chants Explosion incantation",
    "subtitles.frieren_flight.megumin_explosion_charging": "Megumin charges Explosion magic",
    "subtitles.frieren_flight.megumin_explosion_ray": "Explosion beam fires",
    "subtitles.frieren_flight.megumin_explosion_detonation": "EX-PLO-SION! Cataclysmic Detonation!",
    "subtitles.frieren_flight.megumin_explosion_blast": "Explosion shockwave blasts",
    "subtitles.frieren_flight.megumin_explosion": "Full Megumin Explosion cutscene"
}
subtitles_th = {
    "subtitles.frieren_flight.megumin_explosion_chant": "เมกุมินกำลังร่ายบทสวดเอ็กซ์โพลชั่น...",
    "subtitles.frieren_flight.megumin_explosion_charging": "เมกุมินกำลังชาร์จพลังเวทเอ็กซ์โพลชั่น...",
    "subtitles.frieren_flight.megumin_explosion_ray": "ลำแสงเอ็กซ์โพลชั่นยิงทะลวง!",
    "subtitles.frieren_flight.megumin_explosion_detonation": "EX-PLO-SION!! ระเบิดกัมปนาทขั้นสูงสุด!!",
    "subtitles.frieren_flight.megumin_explosion_blast": "คลื่นกระแทกระเบิดกึกก้องสะเทือนมิติ",
    "subtitles.frieren_flight.megumin_explosion": "บทสวดและการระเบิดเอ็กซ์โพลชั่นเต็มรูปแบบ"
}

for res_dir in target_resource_dirs:
    lang_dir = os.path.join(res_dir, "assets", "frieren_flight", "lang")
    os.makedirs(lang_dir, exist_ok=True)
    en_path = os.path.join(lang_dir, "en_us.json")
    th_path = os.path.join(lang_dir, "th_th.json")
    if os.path.exists(en_path):
        with open(en_path, "r", encoding="utf-8") as f:
            en_data = json.load(f)
        en_data.update(subtitles_en)
        with open(en_path, "w", encoding="utf-8") as f:
            json.dump(en_data, f, indent=2, ensure_ascii=False)
    if os.path.exists(th_path):
        with open(th_path, "r", encoding="utf-8") as f:
            th_data = json.load(f)
        th_data.update(subtitles_th)
        with open(th_path, "w", encoding="utf-8") as f:
            json.dump(th_data, f, indent=2, ensure_ascii=False)

print("Updated all sounds.json and lang files successfully!")
