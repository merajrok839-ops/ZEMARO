import zlib
import struct
import math

def create_png_rgba(width, height, pixel_func):
    raw_data = bytearray()
    for y in range(height):
        raw_data.append(0)  # PNG filter byte 0 (None)
        for x in range(width):
            r, g, b, a = pixel_func(x, y, width, height)
            raw_data.extend([int(r), int(g), int(b), int(a)])
    
    def png_chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)
    
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    idat = zlib.compress(bytes(raw_data), 9)
    return b'\x89PNG\r\n\x1a\n' + png_chunk(b'IHDR', ihdr) + png_chunk(b'IDAT', idat) + png_chunk(b'IEND', b'')

def render_zemaro_icon_pixel(x, y, w, h):
    # Normalized coords from 0.0 to 1.0
    nx = x / float(w)
    ny = y / float(h)
    
    # Rounded rectangular white card background with subtle shadow
    card_margin = 0.06
    if nx < card_margin or nx > (1.0 - card_margin) or ny < card_margin or ny > (1.0 - card_margin):
        # Outside card: transparent or dark bg
        return (11, 15, 25, 0)
    
    # Corner radius check for card
    cr = 0.16
    cx = nx if nx < (card_margin + cr) else (nx if nx > (1.0 - card_margin - cr) else 0.5)
    cy = ny if ny < (card_margin + cr) else (ny if ny > (1.0 - card_margin - cr) else 0.5)
    if cx != 0.5 and cy != 0.5:
        dx = (nx - (card_margin + cr)) if nx < 0.5 else (nx - (1.0 - card_margin - cr))
        dy = (ny - (card_margin + cr)) if ny < 0.5 else (ny - (1.0 - card_margin - cr))
        if (dx*dx + dy*dy) > (cr*cr):
            return (11, 15, 25, 0)
            
    # Inside the clean white emblem badge
    # Map coordinates inside the Z emblem space:
    # Emblem center roughly at (0.5, 0.5), bounds from 0.22 to 0.78
    zx = (nx - 0.20) / 0.60
    zy = (ny - 0.20) / 0.60
    
    # Default white card background
    bg_r, bg_g, bg_b, bg_a = (255, 255, 255, 255)
    
    if 0.0 <= zx <= 1.0 and 0.0 <= zy <= 1.0:
        # Top bar of 'Z':
        # Y from 0.15 to 0.38, X from 0.12 to 0.88
        if (0.15 <= zy <= 0.38) and (0.12 <= zx <= 0.88):
            # Angular cut on the right
            if zx < (0.90 - (zy - 0.15) * 0.8):
                t = (zx + zy) / 1.5
                r = int(255)
                g = int(176 - t * 65)
                b = int(0)
                return (r, g, b, 255)
        
        # Diagonal slash of 'Z':
        # Connects top-right (0.80, 0.28) to bottom-left (0.20, 0.82)
        # Line equation: x roughly = 0.80 - (zy - 0.28) * 1.15
        expected_x = 0.80 - (zy - 0.26) * 1.05
        slash_width = 0.26
        if (0.24 <= zy <= 0.84) and ((expected_x - slash_width/2) <= zx <= (expected_x + slash_width/2)):
            t = zy
            r = int(255)
            g = int(185 - t * 105)
            b = int(0)
            return (r, g, b, 255)
            
        # Bottom foot of 'Z':
        if (0.70 <= zy <= 0.86) and (0.12 <= zx <= 0.45):
            t = (zx + zy) / 1.6
            r = int(255)
            g = int(160 - t * 50)
            b = int(0)
            return (r, g, b, 255)
            
        # Upper crystal blue facet:
        # Triangular area in lower right space: zy from 0.52 to 0.70, zx from 0.50 to 0.78
        if (0.50 <= zy <= 0.68) and (0.50 <= zx <= 0.80):
            # check triangle
            if (zx - 0.50) + (zy - 0.50) <= 0.40 and zx >= 0.52 and zy >= 0.52:
                return (90, 160, 248, 255)
                
        # Lower silver facet:
        if (0.72 <= zy <= 0.84) and (0.48 <= zx <= 0.82):
            if (zx - 0.48) + (zy - 0.72) <= 0.42:
                return (220, 235, 252, 255)

    return (bg_r, bg_g, bg_b, bg_a)

def main():
    print("Generating official Zemaro logo icons...")
    for size, filename in [(192, "public/icon-192.png"), (512, "public/icon-512.png")]:
        png_data = create_png_rgba(size, size, render_zemaro_icon_pixel)
        with open(filename, 'wb') as f:
            f.write(png_data)
        # Also copy to root for compatibility
        with open(filename.replace("public/", ""), 'wb') as f:
            f.write(png_data)
        print(f"Generated {filename} ({size}x{size})")

if __name__ == "__main__":
    main()
