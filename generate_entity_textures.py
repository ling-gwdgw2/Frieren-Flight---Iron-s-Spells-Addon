import os
import math
from PIL import Image, ImageDraw, ImageFilter

entity_dirs = [
    'common/src/main/resources/assets/frieren_flight/textures/entity',
    'src/main/resources/assets/frieren_flight/textures/entity'
]
for d in entity_dirs:
    os.makedirs(d, exist_ok=True)

def save_texture(img, filename):
    for base in ['common/src/main/resources/assets/frieren_flight/textures/entity',
                 'src/main/resources/assets/frieren_flight/textures/entity']:
        path = os.path.join(base, filename)
        img.save(path)
        print(f"Saved: {path}")

# =========================================================================
# 1. Reelseiden Slash Texture (512x512)
# Übel's razor-sharp severance crescent blade with translucent cyan-teal glow
# =========================================================================
w_slash, h_slash = 512, 512
img_slash = Image.new('RGBA', (w_slash, h_slash), (0, 0, 0, 0))
d_slash = ImageDraw.Draw(img_slash)

cx, cy = 256, 320 # Center of curvature below, so arc curves upward

# Outer translucent aura layers (Glow)
for r in range(240, 160, -4):
    norm = (r - 160) / 80.0
    alpha = int(75 * math.sin(norm * math.pi))
    # Ethereal cyan-teal glow
    d_slash.arc([cx - r, cy - int(r * 0.75), cx + r, cy + int(r * 0.75)],
                start=200, end=340, fill=(40, 220, 160, alpha), width=5)

# Blade Body (Solid crescent fill using polygon coordinates)
outer_pts = []
inner_pts = []
for deg in range(195, 346, 2):
    rad = math.radians(deg)
    # Outer edge: sharp curve
    ro = 220 + 10 * math.sin((deg - 195) / 150.0 * math.pi)
    xo = cx + ro * math.cos(rad)
    yo = cy + ro * 0.70 * math.sin(rad)
    outer_pts.append((xo, yo))

    # Inner edge: tapers at tips
    ri = 175 - 25 * math.cos((deg - 195) / 150.0 * math.pi * 2)
    xi = cx + ri * math.cos(rad)
    yi = cy + ri * 0.70 * math.sin(rad)
    inner_pts.append((xi, yi))

crescent_poly = outer_pts + list(reversed(inner_pts))
d_slash.polygon(crescent_poly, fill=(50, 230, 175, 140))

# Speed lines / slicing energy channels inside the crescent
for factor in [0.85, 0.90, 0.95]:
    streak_pts = []
    for deg in range(202, 339, 3):
        rad = math.radians(deg)
        r = 180 + (220 - 180) * factor
        x = cx + r * math.cos(rad)
        y = cy + r * 0.70 * math.sin(rad)
        streak_pts.append((x, y))
    d_slash.line(streak_pts, fill=(180, 255, 230, 180), width=2)

# Razor sharp cutting edge (Outer edge)
outer_line = outer_pts
d_slash.line(outer_line, fill=(100, 255, 210, 230), width=4)
d_slash.line(outer_line, fill=(255, 255, 255, 255), width=2)

# Severance spark particles along the cutting edge
for deg in [210, 225, 240, 255, 270, 285, 300, 315, 330]:
    rad = math.radians(deg)
    r = 222
    sx = cx + r * math.cos(rad)
    sy = cy + r * 0.70 * math.sin(rad)
    d_slash.line([(sx - 4, sy - 2), (sx + 4, sy + 2)], fill=(255, 255, 255, 250), width=2)
    d_slash.line([(sx - 2, sy + 4), (sx + 2, sy - 4)], fill=(150, 255, 220, 220), width=2)

save_texture(img_slash, 'reelseiden_slash.png')


# =========================================================================
# 2. Judradjim Lightning Pillar Texture (256x512)
# Denken's Cataclysm Pillar: Dark Void Core with crackling golden lightning
# =========================================================================
w_pil, h_pil = 256, 512
img_pil = Image.new('RGBA', (w_pil, h_pil), (0, 0, 0, 0))
d_pil = ImageDraw.Draw(img_pil)

# Background central beam gradient (Soft black-purple core with amber aura)
for x in range(w_pil):
    dist_from_center = abs(x - 128) / 128.0
    if dist_from_center < 0.85:
        alpha = int(180 * math.cos(dist_from_center * math.pi * 0.5))
        # Inner void core transitioning to amber/gold outer halo
        if dist_from_center < 0.35:
            # Void black core with slight indigo tint
            r_col, g_col, b_col = 20, 10, 35
        else:
            # Amber/Golden lightning aura
            norm_c = (dist_from_center - 0.35) / 0.50
            r_col = int(20 + (255 - 20) * norm_c)
            g_col = int(10 + (190 - 10) * norm_c)
            b_col = int(35 + (50 - 35) * norm_c)
        d_pil.line([(x, 0), (x, h_pil)], fill=(r_col, g_col, b_col, alpha), width=1)

# Multiple continuous branching golden lightning bolts running vertically
import random
random.seed(42)

def draw_vertical_bolt(draw, start_x, width, main_color, glow_color):
    cur_x = start_x
    pts = [(cur_x, 0)]
    for y in range(0, h_pil, 8):
        cur_x += random.choice([-6, -4, -2, 0, 2, 4, 6])
        cur_x = max(60, min(196, cur_x))
        pts.append((cur_x, y))
    pts.append((start_x, h_pil)) # loop to start_x for seamless wrap
    
    # Glow pass
    draw.line(pts, fill=glow_color, width=width + 4)
    # Core lightning pass
    draw.line(pts, fill=main_color, width=width)
    # Pure white hot core
    draw.line(pts, fill=(255, 255, 255, 255), width=max(1, width - 2))

# 3 Major vertical lightning channels
draw_vertical_bolt(d_pil, 110, 4, (255, 230, 90, 240), (220, 140, 20, 100))
draw_vertical_bolt(d_pil, 145, 4, (255, 220, 70, 240), (220, 130, 20, 100))
draw_vertical_bolt(d_pil, 128, 3, (255, 245, 160, 255), (240, 160, 30, 120))

# Electric arcs and sparks bridging across the pillar
for i in range(24):
    y = random.randint(10, h_pil - 10)
    x1 = random.randint(70, 120)
    x2 = random.randint(135, 185)
    ymid = y + random.randint(-15, 15)
    d_pil.line([(x1, y), ((x1 + x2) // 2, ymid), (x2, y)], fill=(255, 240, 140, 220), width=2)
    d_pil.ellipse([x1 - 2, y - 2, x1 + 2, y + 2], fill=(255, 255, 255, 255))
    d_pil.ellipse([x2 - 2, y - 2, x2 + 2, y + 2], fill=(255, 255, 255, 255))

save_texture(img_pil, 'judradjim_pillar.png')

print("\nCUSTOM 3D ENTITY TEXTURES GENERATED SUCCESSFULLY!")
