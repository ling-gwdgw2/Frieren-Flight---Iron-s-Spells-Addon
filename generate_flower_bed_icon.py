import os
import math
from PIL import Image, ImageDraw

def generate_flower_bed_icon():
    size = 128
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2

    # 1. Soft Magical Nature/Ethereal Glow Backdrop
    for r in range(54, 10, -2):
        alpha = int(140 * (1.0 - (r / 54.0) ** 1.5))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(40, 160, 180, alpha))

    # 2. Golden Ring of Magical Runes / Stems
    for angle_deg in range(0, 360, 30):
        rad = math.radians(angle_deg)
        rx = cx + math.cos(rad) * 44
        ry = cy + math.sin(rad) * 44
        draw.ellipse([rx - 2, ry - 2, rx + 2, ry + 2], fill=(255, 230, 120, 180))

    # Stem & Curved Nature Leaves (Bottom)
    stem_pts = [(cx, cy + 10), (cx - 2, cy + 30), (cx + 4, cy + 50)]
    draw.line(stem_pts, fill=(46, 139, 87, 230), width=4)
    # Left Leaf
    draw.polygon([(cx - 2, cy + 28), (cx - 22, cy + 22), (cx - 10, cy + 36)], fill=(60, 179, 113, 240))
    # Right Leaf
    draw.polygon([(cx + 2, cy + 34), (cx + 22, cy + 30), (cx + 10, cy + 44)], fill=(46, 139, 87, 240))

    # 3. Blue Moonweed Flower Petals (5 large celestial blue petals)
    num_petals = 5
    for i in range(num_petals):
        angle = (i / float(num_petals)) * math.pi * 2 - math.pi * 0.5
        tip_r = 34
        mid_r = 20
        spread = 0.42

        p_tip = (cx + math.cos(angle) * tip_r, cy + math.sin(angle) * tip_r)
        p_left = (cx + math.cos(angle - spread) * mid_r, cy + math.sin(angle - spread) * mid_r)
        p_right = (cx + math.cos(angle + spread) * mid_r, cy + math.sin(angle + spread) * mid_r)

        # Outer Petal Body (Deep celestial blue)
        draw.polygon([(cx, cy), p_left, p_tip, p_right], fill=(30, 110, 220, 245))
        # Inner Petal Highlight (Cyan / Aquamarine)
        draw.polygon([(cx, cy),
                      (cx + math.cos(angle - spread * 0.5) * (mid_r * 0.8), cy + math.sin(angle - spread * 0.5) * (mid_r * 0.8)),
                      (cx + math.cos(angle) * (tip_r * 0.85), cy + math.sin(angle) * (tip_r * 0.85)),
                      (cx + math.cos(angle + spread * 0.5) * (mid_r * 0.8), cy + math.sin(angle + spread * 0.5) * (mid_r * 0.8))],
                     fill=(90, 200, 255, 230))
        # Fine white-hot spine line
        draw.line([(cx, cy), p_tip], fill=(220, 245, 255, 200), width=1)

    # Secondary Layer of smaller alternating petals
    for i in range(num_petals):
        angle = ((i + 0.5) / float(num_petals)) * math.pi * 2 - math.pi * 0.5
        tip_r = 24
        mid_r = 14
        spread = 0.35

        p_tip = (cx + math.cos(angle) * tip_r, cy + math.sin(angle) * tip_r)
        p_left = (cx + math.cos(angle - spread) * mid_r, cy + math.sin(angle - spread) * mid_r)
        p_right = (cx + math.cos(angle + spread) * mid_r, cy + math.sin(angle + spread) * mid_r)

        draw.polygon([(cx, cy), p_left, p_tip, p_right], fill=(64, 160, 240, 235))
        draw.polygon([(cx, cy),
                      (cx + math.cos(angle - spread * 0.5) * 10, cy + math.sin(angle - spread * 0.5) * 10),
                      (cx + math.cos(angle) * 18, cy + math.sin(angle) * 18),
                      (cx + math.cos(angle + spread * 0.5) * 10, cy + math.sin(angle + spread * 0.5) * 10)],
                     fill=(160, 235, 255, 250))

    # 4. Brilliant Golden Stamen / Core
    draw.ellipse([cx - 8, cy - 8, cx + 8, cy + 8], fill=(255, 215, 0, 255))
    draw.ellipse([cx - 5, cy - 5, cx + 5, cy + 5], fill=(255, 250, 190, 255))
    draw.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=(255, 255, 255, 255))

    # 5. Sparkling Pollen Particles
    sparkles = [
        (cx - 32, cy - 25, 2), (cx + 35, cy - 22, 2.5), (cx - 20, cy - 38, 1.5),
        (cx + 26, cy + 20, 2), (cx - 36, cy + 15, 1.5), (cx + 15, cy - 42, 2),
        (cx - 10, cy + 42, 2), (cx + 38, cy + 5, 1.5)
    ]
    for sx, sy, sr in sparkles:
        draw.ellipse([sx - sr, sy - sr, sx + sr, sy + sr], fill=(255, 255, 200, 240))
        draw.line([(sx - sr * 2, sy), (sx + sr * 2, sy)], fill=(255, 255, 255, 200), width=1)
        draw.line([(sx, sy - sr * 2), (sx, sy + sr * 2)], fill=(255, 255, 255, 200), width=1)

    target_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'neoforge/src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'forge/src/main/resources/assets/frieren_flight/textures/gui/spell_icons'
    ]

    for d in target_dirs:
        os.makedirs(d, exist_ok=True)
        out_path = os.path.join(d, 'flower_bed.png')
        img.save(out_path)
        print(f"Generated icon: {out_path}")

if __name__ == '__main__':
    generate_flower_bed_icon()
