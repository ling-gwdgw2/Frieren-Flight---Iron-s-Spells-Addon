import os
import soundfile as sf
import numpy as np

base = os.path.dirname(os.path.abspath(__file__))
src_exp = os.path.join(base, "megumin_extracted", "MeguminR", "MeguminR", "sounds", "mob", "megumin_explosion.ogg")

data, sr = sf.read(src_exp)

# 1. Chant climax (28.5s to 33.1s, ~4.6 seconds of Japanese incantation)
start_chant = int(28.5 * sr)
end_chant = int(33.1 * sr)
chant_data = data[start_chant:end_chant]

# 2. Detonation ("EX-PLO-SION!" + massive blast, 33.1s to 36.2s, ~3.1 seconds)
start_det = int(33.1 * sr)
end_det = int(36.2 * sr)
det_data = data[start_det:end_det]

# 3. Pure blast boom (33.95s to 36.2s, ~2.25 seconds of apocalyptic bass blast)
start_blast = int(33.95 * sr)
end_blast = int(36.2 * sr)
blast_data = data[start_blast:end_blast]

out_files = {
    "megumin_explosion_chant.ogg": chant_data,
    "megumin_explosion_detonation.ogg": det_data,
    "megumin_explosion_blast.ogg": blast_data
}

target_dirs = [
    os.path.join(base, "common", "src", "main", "resources", "assets", "frieren_flight", "sounds", "mob"),
    os.path.join(base, "src", "main", "resources", "assets", "frieren_flight", "sounds", "mob"),
    os.path.join(base, "neoforge", "src", "main", "resources", "assets", "frieren_flight", "sounds", "mob"),
    os.path.join(base, "forge", "src", "main", "resources", "assets", "frieren_flight", "sounds", "mob"),
]

for tdir in target_dirs:
    os.makedirs(tdir, exist_ok=True)
    for fname, fdata in out_files.items():
        out_path = os.path.join(tdir, fname)
        sf.write(out_path, fdata, sr, format="OGG", subtype="VORBIS")
        print(f"Written: {out_path} ({len(fdata)} samples, {len(fdata)/sr:.2f}s)")

print("\nAll audio slices generated successfully with perfect timing!")
