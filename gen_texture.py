import os
import struct
import zlib

def make_png(pixels, width, height, palette):
    def p32(n): return struct.pack(">I", n)
    def chunk(type, data):
        return p32(len(data)) + type + data + p32(zlib.crc32(type + data) & 0xffffffff)

    png = b"\x89PNG\r\n\x1a\n"
    # IHDR
    ihdr = p32(width) + p32(height) + struct.pack(">BBBBB", 8, 3, 0, 0, 0)
    png += chunk(b"IHDR", ihdr)
    
    # PLTE
    plte = b"".join(struct.pack(">BBB", r, g, b) for r, g, b in palette)
    png += chunk(b"PLTE", plte)
    
    # tRNS
    trns = b"\x00" # First palette entry is transparent
    png += chunk(b"tRNS", trns)
    
    # IDAT
    raw = b"".join(b"\x00" + bytes(row) for row in pixels)
    idat = zlib.compress(raw)
    png += chunk(b"IDAT", idat)
    
    # IEND
    png += chunk(b"IEND", b"")
    
    return png

width, height = 16, 16
palette = [
    (0, 0, 0),       # 0: transparent
    (34, 34, 34),    # 1: black outline
    (255, 255, 255), # 2: white glass reflection
    (136, 255, 255), # 3: light cyan liquid
    (0, 170, 170),   # 4: dark cyan liquid/edge
    (170, 170, 170), # 5: grey metal (needle)
    (210, 210, 210), # 6: light grey metal (plunger rod/glass)
    (85, 85, 85),    # 7: dark grey plunger rubber
]

grid_str = [
    "0000000000000111",
    "0000000000001661",
    "0000000000011510",
    "0000000000161000",
    "0000000001111000",
    "0000000016271000",
    "0000000162371000",
    "0000001623310000",
    "0000016233410000",
    "0000162334100000",
    "0000111111000000",
    "0001510000000000",
    "0015100000000000",
    "0151000000000000",
    "0110000000000000",
    "0000000000000000",
]

pixels = [[int(c) for c in row] for row in grid_str]

png_data = make_png(pixels, width, height, palette)

target_dir = r"c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\item"
os.makedirs(target_dir, exist_ok=True)

target_path = os.path.join(target_dir, "awesome_syringe.png")
with open(target_path, 'wb') as f:
    f.write(png_data)

print(f"Successfully generated texture at: {target_path}")
