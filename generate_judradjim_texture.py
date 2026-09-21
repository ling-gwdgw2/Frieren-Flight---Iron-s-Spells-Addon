import os
import math
import random
from PIL import Image, ImageDraw, ImageFilter

def generate_seamless_bolt(w, h, start_x, sway, segments=128, rng=None):
    if rng is None:
        rng = random.Random()
    pts = []
    freq1 = rng.uniform(2, 4)
    freq2 = rng.uniform(5, 9)
    phase1 = rng.uniform(0, math.pi * 2)
    phase2 = rng.uniform(0, math.pi * 2)
    
    for i in range(segments + 1):
        t = i / float(segments)
        y = t * h
        offset = (math.sin(t * math.pi * 2 * freq1 + phase1) * sway * 0.65 +
                  math.sin(t * math.pi * 2 * freq2 + phase2) * sway * 0.35 +
                  (rng.random() - 0.5) * 8)
        x = start_x + offset
        pts.append((x, y))
    return pts

def generate_pillar_frame(frame_index, total_frames=6):
    w, h = 512, 1024
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = w // 2
    
    rng = random.Random(2026 + frame_index * 137)

    # 1. Base Plasma Glow Gradient (Horizontal fade, vertically seamless, 100% luminous for shaders)
    for x in range(w):
        dist = abs(x - cx) / float(cx)
        if dist < 0.90:
            alpha_factor = math.cos(dist * math.pi * 0.5) ** 2.2
            
            if dist < 0.18:
                # White-hot central plasma spine
                t = dist / 0.18
                r = 255
                g = int(255 - 20 * t)
                b = int(240 - 160 * t)
                a = int(245 * alpha_factor)
            elif dist < 0.55:
                # Brilliant golden cataclysm corona
                t = (dist - 0.18) / 0.37
                r = int(255 - 15 * t)
                g = int(235 - 75 * t)
                b = int(80 - 60 * t)
                a = int(200 * alpha_factor)
            else:
                # Amber atmospheric aura
                t = (dist - 0.55) / 0.35
                r = int(240 - 70 * t)
                g = int(160 - 110 * t)
                b = 20
                a = int(110 * alpha_factor)
            
            draw.line([(x, 0), (x, h)], fill=(r, g, b, a), width=1)

    # 2. Dynamic Unique Lightning Configurations per Frame
    # Varying bolt count, offsets, and sway per frame
    bolt_configs = []
    if frame_index == 0:
        # Massive central strike with left discharge
        bolt_configs = [
            (cx, 35, (255, 255, 255), (255, 220, 60), 10),
            (cx - 50, 45, (255, 240, 150), (255, 170, 30), 7),
            (cx + 40, 30, (255, 220, 100), (240, 140, 20), 5),
        ]
    elif frame_index == 1:
        # Diagonal zig-zag crossing bolts
        bolt_configs = [
            (cx - 30, 55, (255, 255, 255), (255, 210, 50), 9),
            (cx + 45, 50, (255, 250, 180), (255, 180, 40), 8),
            (cx - 80, 35, (255, 200, 80), (230, 120, 20), 4),
        ]
    elif frame_index == 2:
        # Twin helical braided lightning columns
        bolt_configs = [
            (cx - 25, 40, (255, 255, 220), (255, 200, 60), 8),
            (cx + 25, 40, (255, 255, 220), (255, 200, 60), 8),
            (cx, 20, (255, 255, 255), (255, 230, 100), 10),
            (cx + 85, 30, (255, 190, 70), (220, 110, 10), 4),
        ]
    elif frame_index == 3:
        # Multi-filament electric storm cage
        bolt_configs = [
            (cx, 25, (255, 255, 255), (255, 225, 70), 10),
            (cx - 60, 40, (255, 240, 120), (255, 160, 25), 6),
            (cx + 60, 40, (255, 240, 120), (255, 160, 25), 6),
            (cx - 110, 25, (240, 180, 50), (200, 100, 10), 3),
            (cx + 110, 25, (240, 180, 50), (200, 100, 10), 3),
        ]
    elif frame_index == 4:
        # Intense right-leaning cataclysm strike with dense dark lightning
        bolt_configs = [
            (cx + 35, 50, (255, 255, 255), (255, 215, 60), 10),
            (cx - 45, 35, (255, 230, 110), (255, 150, 20), 7),
            (cx - 95, 30, (255, 200, 70), (210, 110, 15), 4),
        ]
    else: # frame_index == 5
        # Overwhelming cataclysmic climax pulse
        bolt_configs = [
            (cx, 40, (255, 255, 255), (255, 240, 120), 12),
            (cx - 35, 45, (255, 250, 160), (255, 190, 40), 8),
            (cx + 35, 45, (255, 250, 160), (255, 190, 40), 8),
            (cx - 75, 35, (255, 210, 80), (230, 130, 20), 5),
            (cx + 75, 35, (255, 210, 80), (230, 130, 20), 5),
        ]

    # Render primary bolts with multi-pass glow
    for start_x, sway, core_col, glow_col, w_bolt in bolt_configs:
        pts = generate_seamless_bolt(w, h, start_x, sway, segments=128, rng=rng)
        # Wide outer glow
        draw.line(pts, fill=(glow_col[0], glow_col[1], glow_col[2], 85), width=w_bolt + 16)
        # Medium intense glow
        draw.line(pts, fill=(glow_col[0], glow_col[1], glow_col[2], 165), width=w_bolt + 8)
        # High-voltage core
        draw.line(pts, fill=(core_col[0], core_col[1], core_col[2], 235), width=w_bolt)
        # Pure white-hot intense spine (triggers shader HDR bloom!)
        draw.line(pts, fill=(255, 255, 255, 255), width=max(2, w_bolt - 4))

    # 3. Horizontal Plasma Arcs (Violently jumping across the pillar)
    num_bridges = rng.randint(28, 42)
    for _ in range(num_bridges):
        y_pos = rng.uniform(0, h)
        span = rng.uniform(40, 150)
        x_mid = cx + rng.uniform(-40, 40)
        x_left = max(10, x_mid - span * 0.5)
        x_right = min(w - 10, x_mid + span * 0.5)
        
        arc_pts = []
        steps = 8
        for s in range(steps + 1):
            t = s / float(steps)
            ax = x_left + t * (x_right - x_left)
            ay = y_pos + (rng.random() - 0.5) * 26
            arc_pts.append((ax, ay))
            
        draw.line(arc_pts, fill=(255, 220, 90, 150), width=3)
        draw.line(arc_pts, fill=(255, 255, 255, 240), width=1)
        # Arc terminal sparks
        draw.ellipse([x_left - 3, arc_pts[0][1] - 3, x_left + 3, arc_pts[0][1] + 3], fill=(255, 255, 255, 250))
        draw.ellipse([x_right - 3, arc_pts[-1][1] - 3, x_right + 3, arc_pts[-1][1] + 3], fill=(255, 255, 255, 250))

    # 4. Cataclysmic Dark Lightning Veins (Denken's Black Lightning inside Golden Aura)
    # Luminous dark-violet with razor gold outline, zero muddy gray!
    for _ in range(2):
        dark_start_x = cx + rng.uniform(-25, 25)
        v_pts = generate_seamless_bolt(w, h, dark_start_x, 24, segments=96, rng=rng)
        # Gold highlight edge around dark vein
        draw.line(v_pts, fill=(255, 220, 80, 220), width=7)
        # Deep dark cataclysm core vein
        draw.line(v_pts, fill=(50, 15, 75, 240), width=4)
        draw.line(v_pts, fill=(25, 5, 40, 255), width=2)

    return img

def generate_shockwave_ring():
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    c = size // 2
    
    # 1. Concentric shockwave rings
    for r in range(120, 240, 2):
        dist_from_main = abs(r - 190) / 50.0
        if dist_from_main < 1.0:
            alpha = int(220 * (1.0 - dist_from_main))
            draw.ellipse([c - r, c - r, c + r, c + r], outline=(255, 210, 50, alpha), width=2)
            
    # Main intense shock ring
    draw.ellipse([c - 190, c - 190, c + 190, c + 190], outline=(255, 255, 255, 255), width=4)
    draw.ellipse([c - 190, c - 190, c + 190, c + 190], outline=(255, 230, 80, 200), width=10)
    
    # Inner ring
    draw.ellipse([c - 120, c - 120, c + 120, c + 120], outline=(255, 200, 60, 180), width=3)
    draw.ellipse([c - 120, c - 120, c + 120, c + 120], outline=(255, 255, 255, 240), width=1)

    # 2. Radial Lightning Cracks
    rng = random.Random(42)
    for i in range(24):
        angle = (i / 24.0) * math.pi * 2 + rng.uniform(-0.1, 0.1)
        pts = [(c, c)]
        r_current = 20
        while r_current < 230:
            step = rng.uniform(15, 30)
            r_current += step
            angle += rng.uniform(-0.25, 0.25)
            x = c + math.cos(angle) * r_current
            y = c + math.sin(angle) * r_current
            pts.append((x, y))
            
        draw.line(pts, fill=(255, 190, 40, 160), width=4)
        draw.line(pts, fill=(255, 255, 255, 240), width=2)
        
    return img

def main():
    target_dirs = [
        'common/src/main/resources/assets/frieren_flight/textures/entity',
        'src/main/resources/assets/frieren_flight/textures/entity'
    ]

    total_frames = 6
    print(f"Generating {total_frames} animated lightning frames...")
    
    frames = []
    for f in range(total_frames):
        frame_img = generate_pillar_frame(f, total_frames)
        frames.append(frame_img)
        
        for d in target_dirs:
            os.makedirs(d, exist_ok=True)
            out_path = os.path.join(d, f"judradjim_pillar_{f}.png")
            frame_img.save(out_path)
        print(f"  Frame {f} saved successfully!")

    # Also save frame 0 as default judradjim_pillar.png for fallback compatibility
    for d in target_dirs:
        out_path = os.path.join(d, "judradjim_pillar.png")
        frames[0].save(out_path)
    print("  Fallback judradjim_pillar.png saved successfully!")

    # Generate and save shockwave ring
    shockwave_img = generate_shockwave_ring()
    for d in target_dirs:
        out_path = os.path.join(d, "judradjim_shockwave.png")
        shockwave_img.save(out_path)
    print("  Ground Shockwave judradjim_shockwave.png saved successfully!")

    print("All textures generated successfully!")

if __name__ == '__main__':
    main()
