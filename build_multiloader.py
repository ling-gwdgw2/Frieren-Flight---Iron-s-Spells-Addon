import os
import sys
import subprocess
import shutil
import zipfile
import argparse

base_dir = os.path.dirname(os.path.abspath(__file__))

# Find JDK 21
javac_path = r'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\javac.exe'
jar_path = r'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot\bin\jar.exe'

if not os.path.exists(javac_path):
    javac_path = 'javac'
    jar_path = 'jar'

def build_target(target_name):
    print(f"\n==================================================")
    print(f"  BUILDING TARGET: {target_name.upper()}")
    print(f"==================================================")

    if target_name == 'neoforge':
        out_jar = os.path.join(base_dir, 'frieren_flight-neoforge-1.21.1-1.0.0.jar')
        bin_dir = os.path.join(base_dir, 'build', 'neoforge', 'classes')
        libs_cp = os.path.join(base_dir, 'libs', '*')
        src_dirs = [
            os.path.join(base_dir, 'common', 'src', 'main', 'java'),
            os.path.join(base_dir, 'neoforge', 'src', 'main', 'java')
        ]
        res_dirs = [
            os.path.join(base_dir, 'common', 'src', 'main', 'resources'),
            os.path.join(base_dir, 'neoforge', 'src', 'main', 'resources')
        ]
        java_version = '21'
    elif target_name == 'forge':
        out_jar = os.path.join(base_dir, 'frieren_flight-forge-1.20.1-1.0.0.jar')
        bin_dir = os.path.join(base_dir, 'build', 'forge', 'classes')
        libs_cp = os.path.join(base_dir, 'libs_forge', '*')
        src_dirs = [
            os.path.join(base_dir, 'common', 'src', 'main', 'java'),
            os.path.join(base_dir, 'forge', 'src', 'main', 'java')
        ]
        res_dirs = [
            os.path.join(base_dir, 'common', 'src', 'main', 'resources'),
            os.path.join(base_dir, 'forge', 'src', 'main', 'resources')
        ]
        java_version = '17'
    else:
        print(f"Unknown target: {target_name}")
        return False

    if os.path.exists(bin_dir):
        shutil.rmtree(bin_dir)
    os.makedirs(bin_dir, exist_ok=True)

    # 1. Collect Java files
    java_files = []
    for s_dir in src_dirs:
        for root, _, files in os.walk(s_dir):
            for f in files:
                if f.endswith('.java'):
                    java_files.append(os.path.join(root, f))

    print(f"Compiling {len(java_files)} Java files with Java {java_version}...")
    cmd = [javac_path, '-source', java_version, '-target', java_version, '-cp', libs_cp, '-d', bin_dir] + java_files
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"[{target_name}] Compilation failed!")
        print(res.stderr)
        return False
    print(f"[{target_name}] Compilation SUCCESSFUL!")

    # 2. Copy resources
    print(f"[{target_name}] Copying resources...")
    for r_dir in res_dirs:
        if os.path.exists(r_dir):
            for root, _, files in os.walk(r_dir):
                rel = os.path.relpath(root, r_dir)
                target_root = os.path.join(bin_dir, rel) if rel != '.' else bin_dir
                os.makedirs(target_root, exist_ok=True)
                for f in files:
                    src = os.path.join(root, f)
                    dst = os.path.join(target_root, f)
                    shutil.copy2(src, dst)

    # 3. Create JAR archive
    print(f"[{target_name}] Building JAR: {out_jar}")
    cmd_jar = [jar_path, '--create', '--file', out_jar, '-C', bin_dir, '.']
    res_jar = subprocess.run(cmd_jar, capture_output=True, text=True)
    if res_jar.returncode != 0:
        print(f"[{target_name}] Jar creation failed!")
        print(res_jar.stderr)
        return False

    print(f"[{target_name}] SUCCESS! Created: {out_jar} ({os.path.getsize(out_jar)} bytes)")
    
    # 4. Verify contents
    with zipfile.ZipFile(out_jar, 'r') as z:
        print(f"[{target_name}] Total items in JAR: {len(z.namelist())}")
    return True

def main():
    parser = argparse.ArgumentParser(description="Multi-Loader Builder for Frieren Flight")
    parser.add_argument('--target', choices=['neoforge', 'forge', 'all'], default='all', help="Target mod loader")
    args = parser.parse_args()

    targets = ['neoforge', 'forge'] if args.target == 'all' else [args.target]
    success_count = 0

    for t in targets:
        if build_target(t):
            success_count += 1

    print("\n==================================================")
    print(f"  BUILD SUMMARY: {success_count}/{len(targets)} targets built successfully!")
    print("==================================================")

    if success_count == len(targets):
        print("\nAll JARs are ready to play!")
        for t in targets:
            if t == 'neoforge':
                print("  • NeoForge 1.21.1: frieren_flight-neoforge-1.21.1-1.0.0.jar")
            elif t == 'forge':
                print("  • Forge 1.20.1:    frieren_flight-forge-1.20.1-1.0.0.jar")
    else:
        sys.exit(1)

if __name__ == '__main__':
    main()
