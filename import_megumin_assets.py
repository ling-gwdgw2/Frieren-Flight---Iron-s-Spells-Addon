import os
import shutil
from PIL import Image

base = os.path.dirname(os.path.abspath(__file__))
src_sounds = os.path.join(base, "megumin_extracted", "MeguminR", "MeguminR", "sounds", "mob")
src_particles = os.path.join(base, "megumin_extracted", "MeguminR", "MeguminR", "textures", "particle")
src_items = os.path.join(base, "megumin_extracted", "MeguminR", "MeguminR", "textures", "items")
src_entity = os.path.join(base, "megumin_extracted", "MeguminR", "MeguminR", "textures", "entity")

target_resource_dirs = [
    os.path.join(base, "common", "src", "main", "resources"),
    os.path.join(base, "src", "main", "resources"),
    os.path.join(base, "neoforge", "src", "main", "resources"),
    os.path.join(base, "forge", "src", "main", "resources"),
]

# 1. Sound files
sound_files = ["megumin_explosion.ogg", "megumin_explosion_ray.ogg", "megumin_explosion_charging.ogg"]

for res_dir in target_resource_dirs:
    sound_target_dir = os.path.join(res_dir, "assets", "frieren_flight", "sounds", "mob")
    os.makedirs(sound_target_dir, exist_ok=True)
    for sf in sound_files:
        src_path = os.path.join(src_sounds, sf)
        dst_path = os.path.join(sound_target_dir, sf)
        shutil.copy2(src_path, dst_path)
    print(f"Copied sounds to: {sound_target_dir}")

# 2. Textures: resize any image > 1024 to max 1024 on its longest dimension (or 2048 for vertical animation strips)
texture_mappings = {
    "dark_purple_ring.png": "megumin_dark_purple_ring.png",
    "purple_ring.png": "megumin_purple_ring.png",
    "purple_ringaa.png": "megumin_purple_ringaa.png",
    "explosion_circle.png": "megumin_explosion_circle.png",
    "star_eclipse_rainbow.png": "megumin_star_eclipse_rainbow.png",
    "white_star_explode.png": "megumin_white_star_explode.png",
    "explosion_star.png": "megumin_explosion_star.png",
    "explosion_shockwave.png": "megumin_shockwave.png",
    "explosion_shockwave_after.png": "megumin_shockwave_after.png",
    "smoke_hd.png": "megumin_smoke_hd.png",
    "smoke_hd2.png": "megumin_smoke_hd2.png",
    "blue_energy.png": "megumin_blue_energy.png",
    "white_orb.png": "megumin_white_orb.png",
    "black_star.png": "megumin_black_star.png",
    "gradient_star.png": "megumin_gradient_star.png"
}

temp_optimized = os.path.join(base, "build", "temp_textures")
os.makedirs(temp_optimized, exist_ok=True)

for src_name, dst_name in texture_mappings.items():
    src_file = os.path.join(src_particles, src_name)
    dst_temp = os.path.join(temp_optimized, dst_name)
    im = Image.open(src_file)
    w, h = im.size
    
    # Scale down if too huge (e.g. 5000x5000 down to 1024x1024, or 2500x10000 down to 512x2048)
    max_dim = 1024
    if h > w and h > 2048:
        scale = 2048.0 / h
        new_w, new_h = max(1, int(w * scale)), 2048
        im = im.resize((new_w, new_h), Image.Resampling.LANCZOS)
    elif max(w, h) > max_dim:
        scale = max_dim / float(max(w, h))
        new_w, new_h = max(1, int(w * scale)), max(1, int(h * scale))
        im = im.resize((new_w, new_h), Image.Resampling.LANCZOS)
    
    im.save(dst_temp, format="PNG")
    print(f"Processed {dst_name}: from {(w, h)} -> {im.size}")

# Copy to all target resource dirs
for res_dir in target_resource_dirs:
    vfx_dir = os.path.join(res_dir, "assets", "frieren_flight", "textures", "vfx")
    os.makedirs(vfx_dir, exist_ok=True)
    for dst_name in texture_mappings.values():
        shutil.copy2(os.path.join(temp_optimized, dst_name), os.path.join(vfx_dir, dst_name))
    print(f"Copied textures to: {vfx_dir}")

# Also copy staff textures
for res_dir in target_resource_dirs:
    item_tex_dir = os.path.join(res_dir, "assets", "frieren_flight", "textures", "item")
    os.makedirs(item_tex_dir, exist_ok=True)
    staff_src = os.path.join(src_items, "megumin_staff.png")
    if os.path.exists(staff_src):
        shutil.copy2(staff_src, os.path.join(item_tex_dir, "megumin_staff.png"))

print("\nAll assets imported and optimized successfully!")
