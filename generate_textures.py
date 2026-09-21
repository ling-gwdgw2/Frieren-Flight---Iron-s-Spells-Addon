import os
import math
from PIL import Image, ImageDraw, ImageFilter

dirs = [
    'common/src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
    'common/src/main/resources/assets/frieren_flight/textures/mob_effect',
    'src/main/resources/assets/frieren_flight/textures/gui/spell_icons',
    'src/main/resources/assets/frieren_flight/textures/mob_effect'
]
for d in dirs:
    os.makedirs(d, exist_ok=True)

w, h = 128, 128
cx, cy = 64, 64

def save_to_all(img, filename, is_effect=False):
    sub = 'mob_effect' if is_effect else 'gui/spell_icons'
    for base in ['common/src/main/resources/assets/frieren_flight/textures', 'src/main/resources/assets/frieren_flight/textures']:
        path = os.path.join(base, sub, filename)
        img.save(path)
        print(f"Saved: {path}")

# ==========================================
# 1. Flight Magic Spell Icon (128x128)
# ==========================================
img_flight = Image.new('RGBA', (w, h), (0, 0, 0, 0))
draw = ImageDraw.Draw(img_flight)

for r in range(54, 40, -2):
    alpha = int(40 * (1 - (r - 40) / 14))
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(100, 210, 255, alpha), width=2)

draw.ellipse([cx - 42, cy - 42, cx + 42, cy + 42], outline=(140, 230, 255, 230), width=2)
draw.ellipse([cx - 36, cy - 36, cx + 36, cy + 36], outline=(100, 200, 255, 180), width=1)

diamond_r = 39
for angle in range(0, 360, 90):
    rad1 = math.radians(angle)
    rad2 = math.radians(angle + 90)
    x1, y1 = cx + diamond_r * math.cos(rad1), cy + diamond_r * math.sin(rad1)
    x2, y2 = cx + diamond_r * math.cos(rad2), cy + diamond_r * math.sin(rad2)
    draw.line([(x1, y1), (x2, y2)], fill=(180, 240, 255, 200), width=1)

for angle in range(0, 360, 45):
    rad = math.radians(angle)
    nx, ny = cx + 42 * math.cos(rad), cy + 42 * math.sin(rad)
    draw.ellipse([nx - 2, ny - 2, nx + 2, ny + 2], fill=(255, 255, 255, 255))

draw.ellipse([cx - 5, cy - 28, cx + 5, cy - 18], fill=(240, 250, 255, 255))
draw.line([(cx - 4, cy - 24), (cx - 14, cy - 18)], fill=(220, 245, 255, 220), width=2)
draw.line([(cx + 4, cy - 24), (cx + 14, cy - 18)], fill=(220, 245, 255, 220), width=2)
draw.polygon([(cx - 6, cy - 17), (cx + 6, cy - 17), (cx + 8, cy - 2), (cx - 8, cy - 2)], fill=(200, 235, 255, 240))
draw.polygon([(cx - 8, cy - 2), (cx + 8, cy - 2), (cx + 14, cy + 18), (cx - 14, cy + 18)], fill=(160, 220, 255, 230))
draw.line([(cx + 6, cy - 10), (cx + 20, cy - 32)], fill=(255, 220, 140, 255), width=2)
draw.ellipse([cx + 18, cy - 35, cx + 24, cy - 29], outline=(255, 240, 180, 255), width=2)
draw.line([(cx - 4, cy + 18), (cx - 3, cy + 24)], fill=(180, 230, 255, 240), width=2)
draw.line([(cx + 4, cy + 18), (cx + 5, cy + 24)], fill=(180, 230, 255, 240), width=2)
draw.ellipse([cx - 16, cy + 24, cx + 16, cy + 28], outline=(200, 250, 255, 240), width=2)
draw.ellipse([cx - 22, cy + 28, cx + 22, cy + 32], outline=(140, 220, 255, 180), width=1)

save_to_all(img_flight, 'flight.png')

# ==========================================
# 2. Judradjim (Cataclysm Lightning) Spell Icon (128x128)
# Denken's massive gold-black destructive lightning
# ==========================================
img_jud = Image.new('RGBA', (w, h), (0, 0, 0, 0))
d_jud = ImageDraw.Draw(img_jud)

# Golden/dark shockwave outer glow
for r in range(56, 42, -2):
    alpha = int(50 * (1 - (r - 42) / 14))
    d_jud.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(255, 200, 50, alpha), width=2)

# Geometric Ancient Combat Magic Ring (Dark purple-black with golden trim)
d_jud.ellipse([cx - 45, cy - 45, cx + 45, cy + 45], outline=(255, 220, 80, 240), width=2)
d_jud.ellipse([cx - 38, cy - 38, cx + 38, cy + 38], outline=(60, 20, 90, 220), width=3)
d_jud.ellipse([cx - 34, cy - 34, cx + 34, cy + 34], outline=(255, 180, 40, 190), width=1)

# Inscribed 8-point star / rune octagram
for i in range(8):
    a1 = math.radians(i * 45)
    a2 = math.radians(i * 45 + 135)
    x1, y1 = cx + 40 * math.cos(a1), cy + 40 * math.sin(a1)
    x2, y2 = cx + 40 * math.cos(a2), cy + 40 * math.sin(a2)
    d_jud.line([(x1, y1), (x2, y2)], fill=(255, 215, 60, 160), width=1)

# Central Cataclysmic Lightning Bolt (Thick zigzag with inner gold & black core)
bolt_pts = [
    (64, 14), (54, 38), (68, 42), (48, 70), (66, 72), (46, 114)
]
# Glow under bolt
for off in [-3, -2, -1, 1, 2, 3]:
    glow_pts = [(x + off, y) for x, y in bolt_pts]
    d_jud.line(glow_pts, fill=(255, 160, 20, 120), width=4)

# Main Golden Lightning
d_jud.line(bolt_pts, fill=(255, 245, 180, 255), width=5)
d_jud.line(bolt_pts, fill=(255, 255, 255, 255), width=2)

# Secondary Black-Purple Thunder Tendrils
tendril_1 = [(54, 38), (38, 52), (42, 66)]
tendril_2 = [(68, 42), (86, 56), (78, 76), (92, 94)]
tendril_3 = [(66, 72), (80, 84), (74, 102)]
d_jud.line(tendril_1, fill=(90, 20, 140, 230), width=3)
d_jud.line(tendril_1, fill=(255, 220, 100, 220), width=1)
d_jud.line(tendril_2, fill=(90, 20, 140, 230), width=3)
d_jud.line(tendril_2, fill=(255, 220, 100, 220), width=1)
d_jud.line(tendril_3, fill=(90, 20, 140, 230), width=3)
d_jud.line(tendril_3, fill=(255, 220, 100, 220), width=1)

# Impact explosion spark at bottom
d_jud.ellipse([46 - 8, 114 - 4, 46 + 8, 114 + 4], outline=(255, 230, 120, 240), width=2)
d_jud.line([(46 - 12, 114), (46 + 12, 114)], fill=(255, 255, 255, 255), width=2)

save_to_all(img_jud, 'judradjim.png')

# ==========================================
# 3. Reelseiden (Cleaving Slash) Spell Icon (128x128)
# Übel's clean, razor-sharp crescent severance cut
# ==========================================
img_reel = Image.new('RGBA', (w, h), (0, 0, 0, 0))
d_reel = ImageDraw.Draw(img_reel)

# Dark emerald-teal aura (Übel's signature anime color palette)
for r in range(54, 38, -2):
    alpha = int(45 * (1 - (r - 38) / 16))
    d_reel.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(30, 180, 120, alpha), width=2)

# Severed / Split Outer Ring (showing the magic cuts through the spell circle itself!)
d_reel.arc([cx - 44, cy - 44, cx + 44, cy + 44], start=45, end=210, fill=(80, 230, 170, 230), width=2)
d_reel.arc([cx - 44, cy - 44, cx + 44, cy + 44], start=225, end=390, fill=(80, 230, 170, 230), width=2)

# Inner sliced circle offset slightly to demonstrate physical cleavage
d_reel.arc([cx - 36 - 2, cy - 36 - 2, cx + 36 - 2, cy + 36 - 2], start=40, end=215, fill=(40, 160, 120, 180), width=2)
d_reel.arc([cx - 36 + 2, cy - 36 + 2, cx + 36 + 2, cy + 36 + 2], start=220, end=395, fill=(40, 160, 120, 180), width=2)

# Main Diagonal Cleave / Severance Blade (from top-right to bottom-left)
# Translucent wide wind-blade cone
blade_poly = [
    (108, 20), (114, 24), (24, 114), (18, 108)
]
d_reel.polygon(blade_poly, fill=(120, 255, 200, 100))

# Bright Razor Sharp cutting line
d_reel.line([(116, 18), (14, 116)], fill=(50, 220, 160, 180), width=6)
d_reel.line([(116, 18), (14, 116)], fill=(200, 255, 240, 240), width=3)
d_reel.line([(116, 18), (14, 116)], fill=(255, 255, 255, 255), width=1)

# Secondary Cross-Cleave (X-cut / Scissors intersection)
d_reel.line([(18, 26), (102, 106)], fill=(80, 240, 180, 150), width=3)
d_reel.line([(18, 26), (102, 106)], fill=(240, 255, 250, 230), width=1)

# Severance spark particles flying off the cut line
sparks = [
    (60, 52), (72, 64), (52, 68), (82, 48), (44, 86), (90, 80)
]
for sx, sy in sparks:
    d_reel.line([(sx - 4, sy - 1), (sx + 4, sy + 1)], fill=(255, 255, 255, 250), width=2)
    d_reel.line([(sx, sy - 3), (sx, sy + 3)], fill=(120, 255, 210, 200), width=1)

save_to_all(img_reel, 'reelseiden.png')

print("\nALL TEXTURES SUCCESSFULLY GENERATED!")
