import os
import json

dirs = [
    'common/src/main/resources/assets/frieren_flight/lang',
    'src/main/resources/assets/frieren_flight/lang',
    'neoforge/src/main/resources/assets/frieren_flight/lang',
    'forge/src/main/resources/assets/frieren_flight/lang'
]

en_additions = {
    "spell.frieren_flight.explosion": "Explosion Magic",
    "spell.frieren_flight.explosion.desc": "Megumin's iconic apocalyptic detonation magic (エクスプロージョン). The caster chants an ancient incantation while firmly rooted in place for 4.0 seconds, before unleashing a cataclysmic blast that conjures a colossal 50m 3D mushroom cloud and shockwave, inflicting 850–1,150 damage, carving a massive crater into the earth, and draining 100% of remaining mana.",
    "ui.frieren_flight.mana_drain_all": "Mana Cost: 100% (All Remaining Mana)"
}

th_additions = {
    "spell.frieren_flight.explosion": "เวทมนตร์ระเบิดกัมปนาท (Explosion Magic)",
    "spell.frieren_flight.explosion.desc": "สุดยอดเวทมนตร์ระเบิดกัมปนาทของเมกุมิน (エクスプロージョン) ปักหลักยืนร่ายบทสวดโบราณเป็นเวลา 4 วินาที ก่อนปลดปล่อยพลังทำลายล้างมหาศาล เสกเสาเพลิงและเห็ดควันยักษ์ 3D สูง 50 บล็อก พร้อมคลื่นกระแทก 48 บล็อก ทำดาเมจ 850–1,150 ดาเมจ เจาะหลุมอุกกาบาตลึกลงไปในผืนดิน และเผาผลาญมานาทั้งหมดจนกลายเป็น 0!",
    "ui.frieren_flight.mana_drain_all": "การใช้มานา: 100% (ดูดมานาทั้งหมดจนหมดหลอด)"
}

for d in dirs:
    os.makedirs(d, exist_ok=True)
    en_path = os.path.join(d, 'en_us.json')
    if os.path.exists(en_path):
        with open(en_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
    else:
        data = {}
    data.update(en_additions)
    with open(en_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    th_path = os.path.join(d, 'th_th.json')
    if os.path.exists(th_path):
        with open(th_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
    else:
        data = {}
    data.update(th_additions)
    with open(th_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print("All language files updated!")
