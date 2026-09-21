import os
import math
import random
from PIL import Image, ImageDraw, ImageFilter

def generate_flower_bed_field():
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    max_r = size // 2 - 8

    # 1. Soft Atmospheric Radiant Corona (Falloff from center to max_r)
    for r in range(max_r, 10, -2):
        dist = r / float(max_r)
        alpha = int(120 * (1.0 - dist) * (math.sin(dist * math.pi) ** 0.8))
        # Cyan-blue to emerald gradient
        cr = int(30 + 40 * dist)
        cg = int(180 - 60 * dist)
        cb = int(240 - 20 * dist)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(cr, cg, cb, alpha))

    # 2. Concentric Magical Mandala Rings & Floral Vines
    ring_radii = [max_r - 2, max_r - 20, max_r - 55, 140, 75]
    for r in ring_radii:
        # Subtle glowing ring
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(90, 210, 255, 160), width=3)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(255, 255, 255, 220), width=1)

    # Elven Rune Dots & Petal Nodes along outer ring
    for angle_deg in range(0, 360, 10):
        rad = math.radians(angle_deg)
        rx = cx + math.cos(rad) * (max_r - 11)
        ry = cy + math.sin(rad) * (max_r - 11)
        draw.ellipse([rx - 2, ry - 2, rx + 2, ry + 2], fill=(255, 240, 160, 230))

    # 3. Eight-Fold Sacred Floral Star (Frieren's Magic Circle Motif)
    for i in range(8):
        a1 = math.radians(i * 45)
        a2 = math.radians((i + 1) * 45)
        p1 = (cx + math.cos(a1) * (max_r - 55), cy + math.sin(a1) * (max_r - 55))
        p2 = (cx + math.cos(a2) * (max_r - 55), cy + math.sin(a2) * (max_r - 55))
        draw.line([p1, p2], fill=(120, 230, 255, 180), width=2)
        draw.line([(cx, cy), p1], fill=(80, 180, 240, 130), width=1)

    # 4. Dense Clusters of Blooming Blue Moonweeds (Petals & Stamens)
    rng = random.Random(2026)
    
    def draw_single_flower(fx, fy, scale=1.0, is_blue=True):
        petals = 5
        rot_offset = rng.uniform(0, math.pi * 2)
        tip_len = 10 * scale
        
        # Petal colors
        if is_blue:
            col_outer = (35, 130, 245, 230)
            col_inner = (130, 225, 255, 245)
        else: # White/Pink contrast
            col_outer = (240, 245, 255, 220)
            col_inner = (255, 200, 230, 240)

        for p in range(petals):
            p_ang = rot_offset + (p / float(petals)) * math.pi * 2
            tx = fx + math.cos(p_ang) * tip_len
            ty = fy + math.sin(p_ang) * tip_len
            lx = fx + math.cos(p_ang - 0.4) * (tip_len * 0.6)
            ly = fy + math.sin(p_ang - 0.4) * (tip_len * 0.6)
            rx = fx + math.cos(p_ang + 0.4) * (tip_len * 0.6)
            ry = fy + math.sin(p_ang + 0.4) * (tip_len * 0.6)
            draw.polygon([(fx, fy), (lx, ly), (tx, ty), (rx, ry)], fill=col_outer)
            draw.polygon([(fx, fy), (tx, ty)], fill=col_inner)
            
        # Golden Center Stamen
        cr = 3 * scale
        draw.ellipse([fx - cr, fy - cr, fx + cr, fy + cr], fill=(255, 220, 50, 255))
        draw.ellipse([fx - cr*0.5, fy - cr*0.5, fx + cr*0.5, fy + cr*0.5], fill=(255, 255, 255, 255))

    # Populate ~160 flowers across the field
    for _ in range(160):
        # Distribute with density peaking around middle radius
        r = rng.triangular(15, max_r - 25, (max_r - 25) * 0.65)
        angle = rng.uniform(0, math.pi * 2)
        fx = cx + math.cos(angle) * r
        fy = cy + math.sin(angle) * r
        scale = rng.uniform(0.7, 1.3)
        is_blue = rng.random() < 0.75 # 75% Blue Moonweeds!
        draw_single_flower(fx, fy, scale, is_blue)

    # 5. Brilliant Center Blooming Flower Emblem (Frieren's Masterpiece Moonweed)
    draw_single_flower(cx, cy, scale=2.8, is_blue=True)
    for c_ring in range(8):
        c_ang = math.radians(c_ring * 45)
        draw_single_flower(cx + math.cos(c_ang) * 28, cy + math.sin(c_ang) * 28, scale=1.4, is_blue=True)

    return img

def generate_blue_moonweed_petal():
    size = 128
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2

    # A single, graceful curved 3D Blue Moonweed petal
    pts = [
        (cx, cy + 42),
        (cx - 24, cy + 18),
        (cx - 30, cy - 10),
        (cx - 15, cy - 36),
        (cx, cy - 48), # Tip
        (cx + 15, cy - 36),
        (cx + 30, cy - 10),
        (cx + 24, cy + 18),
    ]

    # Outer Petal Glow
    draw.polygon(pts, fill=(40, 130, 240, 220))
    
    # Inner Vibrant Cyan Shimmer
    inner_pts = [
        (cx, cy + 30),
        (cx - 16, cy + 12),
        (cx - 18, cy - 8),
        (cx - 8, cy - 28),
        (cx, cy - 38),
        (cx + 8, cy - 28),
        (cx + 18, cy - 8),
        (cx + 16, cy + 12),
    ]
    draw.polygon(inner_pts, fill=(120, 220, 255, 240))
    
    # Central Translucent Spine
    draw.line([(cx, cy + 36), (cx, cy - 44)], fill=(230, 250, 255, 250), width=2)
    # Dewdrop highlight
    draw.ellipse([cx - 4, cy - 18, cx + 4, cy - 10], fill=(255, 255, 255, 230))

    return img

def generate_mob_effect_icon():
    # 18x18 inventory status effect icon for Flower Blessing
    size = 18
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 5-petal mini blue moonweed
    c = 9
    for p in range(5):
        ang = (p / 5.0) * math.pi * 2 - math.pi * 0.5
        x = c + math.cos(ang) * 5
        y = c + math.sin(ang) * 5
        draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=(50, 150, 255, 240))
        
    # Center golden core
    draw.ellipse([c - 2, c - 2, c + 2, c + 2], fill=(255, 215, 0, 255))
    draw.point((c, c), fill=(255, 255, 255, 255))
    
    return img

def main():
    target_entity_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/entity',
        'src/main/resources/assets/frieren_flight/textures/entity',
        'neoforge/src/main/resources/assets/frieren_flight/textures/entity',
        'forge/src/main/resources/assets/frieren_flight/textures/entity'
    ]

    target_effect_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/mob_effect',
        'src/main/resources/assets/frieren_flight/textures/mob_effect',
        'neoforge/src/main/resources/assets/frieren_flight/textures/mob_effect',
        'forge/src/main/resources/assets/frieren_flight/textures/mob_effect'
    ]

    print("Generating Flower Bed 3D VFX Textures...")
    field_img = generate_flower_bed_field()
    petal_img = generate_blue_moonweed_petal()
    effect_img = generate_mob_effect_icon()

    for d in target_entity_dirs:
        os.makedirs(d, exist_ok=True)
        field_img.save(os.path.join(d, 'flower_bed_field.png'))
        petal_img.save(os.path.join(d, 'blue_moonweed_petal.png'))
        print(f"Saved entity textures to: {d}")

    for d in target_effect_dirs:
        os.makedirs(d, exist_ok=True)
        effect_img.save(os.path.join(d, 'flower_blessing.png'))
        print(f"Saved mob effect icon to: {d}")

    print("All textures created successfully!")

if __name__ == '__main__':
    main()
