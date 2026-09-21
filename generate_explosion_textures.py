import os
import math
import random
from PIL import Image, ImageDraw, ImageFilter

def generate_explosion_icon():
    size = 128
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    max_r = size // 2 - 4

    # 1. Dark Crimson / Obsidian Vignette Background
    for r in range(max_r, 4, -1):
        dist = r / float(max_r)
        alpha = int(255 * (1.0 - dist * 0.4))
        # Dark crimson to fiery red
        cr = int(35 + 160 * (1.0 - dist))
        cg = int(5 + 40 * (1.0 - dist))
        cb = int(10 + 20 * (1.0 - dist))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(cr, cg, cb, alpha))

    # 2. Golden-Crimson Megumin Magic Runes Outer Border
    draw.ellipse([cx - max_r, cy - max_r, cx + max_r, cy + max_r], outline=(255, 180, 40, 240), width=3)
    draw.ellipse([cx - max_r + 4, cy - max_r + 4, cx + max_r - 4, cy + max_r - 4], outline=(255, 60, 20, 200), width=1)

    # Rune nodes along circle
    for deg in range(0, 360, 30):
        rad = math.radians(deg)
        rx = cx + math.cos(rad) * (max_r - 2)
        ry = cy + math.sin(rad) * (max_r - 2)
        draw.ellipse([rx - 3, ry - 3, rx + 3, ry + 3], fill=(255, 230, 80, 255))

    # 3. Inner Sacred Explosive Pentagram / Star Motif
    pts = []
    for i in range(5):
        a1 = math.radians(i * 144 - 90)
        pts.append((cx + math.cos(a1) * (max_r - 12), cy + math.sin(a1) * (max_r - 12)))
    draw.polygon(pts, outline=(255, 220, 60, 240), width=2)

    # 4. Central Fiery Detonation Core & Mushroom Cloud Silhouette
    # Fiery rays
    for deg in range(0, 360, 15):
        rad = math.radians(deg)
        length = 18 if deg % 30 == 0 else 30
        ex = cx + math.cos(rad) * length
        ey = cy + math.sin(rad) * length
        col = (255, 240, 120, 250) if deg % 30 == 0 else (255, 90, 20, 220)
        draw.line([(cx, cy), (ex, ey)], fill=col, width=2)

    # Mushroom Cloud Cap in center
    draw.ellipse([cx - 24, cy - 28, cx + 24, cy - 4], fill=(255, 80, 10, 245))
    draw.ellipse([cx - 18, cy - 25, cx + 18, cy - 8], fill=(255, 180, 40, 255))
    draw.ellipse([cx - 10, cy - 22, cx + 10, cy - 12], fill=(255, 255, 200, 255))

    # Central Column
    draw.polygon([(cx - 12, cy - 6), (cx + 12, cy - 6), (cx + 18, cy + 22), (cx - 18, cy + 22)], fill=(255, 110, 20, 245))
    draw.polygon([(cx - 6, cy - 6), (cx + 6, cy - 6), (cx + 8, cy + 20), (cx - 8, cy + 20)], fill=(255, 230, 80, 255))

    # Ground blast ring
    draw.ellipse([cx - 28, cy + 16, cx + 28, cy + 28], outline=(255, 240, 90, 250), width=2)

    return img

def generate_explosion_shockwave():
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    max_r = size // 2 - 8

    # Concentric Fiery Shockwave Rings with falloff
    for r in range(max_r, 20, -2):
        dist = r / float(max_r)
        # Ring intensity peak near outer 70%-90%
        intensity = math.sin(dist * math.pi) ** 1.8
        alpha = int(220 * intensity)
        # Crimson to gold to white gradient
        if dist > 0.7:
            cr, cg, cb = 255, int(80 + 140 * (dist - 0.7) / 0.3), 30
        else:
            cr, cg, cb = int(180 + 75 * dist / 0.7), int(30 * dist / 0.7), 10
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(cr, cg, cb, alpha))

    # Sharp Glowing Rune Rings
    ring_radii = [max_r - 4, max_r - 25, max_r - 60, 160, 90]
    for r in ring_radii:
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(255, 220, 50, 240), width=3)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(255, 255, 255, 255), width=1)

    # Megumin Ancient Crimson Runes along circumference
    for deg in range(0, 360, 8):
        rad = math.radians(deg)
        rx = cx + math.cos(rad) * (max_r - 14)
        ry = cy + math.sin(rad) * (max_r - 14)
        draw.ellipse([rx - 3, ry - 3, rx + 3, ry + 3], fill=(255, 240, 100, 255))

    # Radiating Explosive Rays
    for deg in range(0, 360, 15):
        rad = math.radians(deg)
        p1 = (cx + math.cos(rad) * 60, cy + math.sin(rad) * 60)
        p2 = (cx + math.cos(rad) * (max_r - 25), cy + math.sin(rad) * (max_r - 25))
        draw.line([p1, p2], fill=(255, 140, 30, 200), width=2)

    return img

def generate_explosion_pillar():
    w, h = 256, 512
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = w // 2

    # Vertical Plasma Flames & Lightning Column
    rng = random.Random(42)

    for y in range(h):
        y_norm = y / float(h)
        # Core width fluctuates
        col_w = int(w * 0.42 * (0.8 + 0.2 * math.sin(y * 0.05)))
        for x in range(cx - col_w, cx + col_w):
            dx = abs(x - cx) / float(col_w)
            alpha = int(240 * (1.0 - dx ** 2))
            if dx < 0.25:
                # White-hot plasma core
                draw.point((x, y), fill=(255, 255, 240, alpha))
            elif dx < 0.6:
                # Golden-yellow fire
                draw.point((x, y), fill=(255, 200, 40, alpha))
            else:
                # Deep crimson & black smoke edge
                draw.point((x, y), fill=(220, 40, 10, alpha))

    # Jagged Black Lightning Arcs traversing the column
    for arc in range(6):
        cur_x = cx + rng.randint(-30, 30)
        for y in range(0, h, 14):
            next_x = cur_x + rng.randint(-18, 18)
            next_x = max(cx - 70, min(cx + 70, next_x))
            draw.line([(cur_x, y), (next_x, y + 14)], fill=(20, 5, 10, 250), width=3)
            # Crimson discharge corona
            draw.line([(cur_x, y), (next_x, y + 14)], fill=(255, 60, 20, 160), width=6)
            cur_x = next_x

    return img

def generate_explosion_cloud():
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    max_r = size // 2 - 10

    # Billowing Mushroom Cloud Cap / Torus with deep crimson, orange, and smoke billows
    rng = random.Random(1337)

    # 1. Base radial fiery glow
    for r in range(max_r, 10, -2):
        dist = r / float(max_r)
        alpha = int(240 * (1.0 - dist) * (0.8 + 0.2 * math.sin(dist * math.pi * 3)))
        cr = int(255 - 60 * dist)
        cg = int(140 * (1.0 - dist))
        cb = int(20 * (1.0 - dist))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(cr, cg, cb, alpha))

    # 2. Dense overlapping smoke & fire billows (Blobs of billowing explosion)
    for _ in range(220):
        angle = rng.uniform(0, math.pi * 2)
        dist = rng.triangular(20, max_r - 20, (max_r - 20) * 0.7)
        bx = cx + math.cos(angle) * dist
        by = cy + math.sin(angle) * dist
        br = rng.uniform(22, 55)

        # Color variation: from core white-hot to outer volcanic ash
        rel_dist = dist / float(max_r)
        if rel_dist < 0.35:
            col = (255, 230, 120, rng.randint(200, 255))
        elif rel_dist < 0.7:
            col = (240, 80, 20, rng.randint(180, 240))
        else:
            # Dark smoke with glowing ember tinge
            gray = rng.randint(25, 65)
            col = (gray + 50, gray + 15, gray, rng.randint(160, 230))

        draw.ellipse([bx - br, by - br, bx + br, by + br], fill=col)

    # 3. Inner White-Gold Supernova Core
    for cr in range(60, 5, -2):
        c_alpha = int(255 * (1.0 - cr / 60.0))
        draw.ellipse([cx - cr, cy - cr, cx + cr, cy + cr], fill=(255, 255, 220, c_alpha))

    return img

def main():
    icon_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'neoforge/src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
        'forge/src/main/resources/assets/frieren_flight/textures/gui/spell_icons'
    ]

    entity_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/entity',
        'src/main/resources/assets/frieren_flight/textures/entity',
        'neoforge/src/main/resources/assets/frieren_flight/textures/entity',
        'forge/src/main/resources/assets/frieren_flight/textures/entity'
    ]

    print("Generating Megumin Explosion HD Textures...")
    icon = generate_explosion_icon()
    shockwave = generate_explosion_shockwave()
    pillar = generate_explosion_pillar()
    cloud = generate_explosion_cloud()

    for d in icon_dirs:
        os.makedirs(d, exist_ok=True)
        icon.save(os.path.join(d, 'explosion.png'))
        print(f"Saved icon to {d}")

    for d in entity_dirs:
        os.makedirs(d, exist_ok=True)
        shockwave.save(os.path.join(d, 'explosion_shockwave.png'))
        pillar.save(os.path.join(d, 'explosion_pillar.png'))
        cloud.save(os.path.join(d, 'explosion_cloud.png'))
        print(f"Saved entity textures to {d}")

    print("All Explosion textures generated successfully!")

if __name__ == '__main__':
    main()
