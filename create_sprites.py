#!/usr/bin/env python3
"""
Script to create simple pixel art sprites for the Free Friends In School Game.
Creates placeholder sprites that can be replaced later with higher-quality ones from OpenGameArt.
"""

try:
    from PIL import Image, ImageDraw
except ImportError:
    print("Pillow not installed. Installing...")
    import subprocess
    subprocess.check_call(["pip", "install", "Pillow"])
    from PIL import Image, ImageDraw

import os

def create_player_sprite():
    """Create a simple player (kid) sprite - 40x40 pixels"""
    img = Image.new('RGBA', (40, 40), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Head (circle)
    draw.ellipse([12, 8, 28, 24], fill=(255, 220, 177))  # Skin color
    
    # Body (shirt - blue)
    draw.rectangle([14, 24, 26, 36], fill=(50, 100, 200))
    
    # Arms
    draw.rectangle([10, 26, 14, 34], fill=(255, 220, 177))  # Left arm
    draw.rectangle([26, 26, 30, 34], fill=(255, 220, 177))  # Right arm
    
    # Legs (pants - darker blue)
    draw.rectangle([16, 36, 20, 40], fill=(30, 60, 150))
    draw.rectangle([20, 36, 24, 40], fill=(30, 60, 150))
    
    # Eyes
    draw.ellipse([16, 14, 18, 16], fill=(0, 0, 0))  # Left eye
    draw.ellipse([22, 14, 24, 16], fill=(0, 0, 0))  # Right eye
    
    # Mouth (smile)
    draw.arc([17, 17, 23, 21], start=0, end=180, fill=(0, 0, 0), width=1)
    
    return img

def create_teacher_sprite():
    """Create a simple teacher sprite - 40x40 pixels"""
    img = Image.new('RGBA', (40, 40), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Head (circle)
    draw.ellipse([12, 6, 28, 22], fill=(255, 200, 150))  # Skin color
    
    # Body (shirt - red/dark)
    draw.rectangle([14, 22, 26, 36], fill=(180, 50, 50))
    
    # Arms
    draw.rectangle([10, 24, 14, 34], fill=(255, 200, 150))  # Left arm
    draw.rectangle([26, 24, 30, 34], fill=(255, 200, 150))  # Right arm
    
    # Legs (pants - dark)
    draw.rectangle([16, 36, 20, 40], fill=(50, 50, 50))
    draw.rectangle([20, 36, 24, 40], fill=(50, 50, 50))
    
    # Eyes (glasses effect)
    draw.ellipse([14, 12, 20, 18], outline=(0, 0, 0), width=2)  # Left lens
    draw.ellipse([20, 12, 26, 18], outline=(0, 0, 0), width=2)  # Right lens
    draw.line([20, 14, 20, 16], fill=(0, 0, 0), width=1)  # Bridge
    
    # Mouth (neutral/straight)
    draw.line([17, 19, 23, 19], fill=(0, 0, 0), width=1)
    
    return img

def create_friend_sprite():
    """Create a simple friend sprite - 30x30 pixels"""
    img = Image.new('RGBA', (30, 30), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Head (circle)
    draw.ellipse([9, 6, 21, 18], fill=(255, 220, 177))  # Skin color
    
    # Body (shirt - green)
    draw.rectangle([11, 18, 19, 26], fill=(50, 200, 100))
    
    # Arms
    draw.rectangle([8, 20, 11, 26], fill=(255, 220, 177))  # Left arm
    draw.rectangle([19, 20, 22, 26], fill=(255, 220, 177))  # Right arm
    
    # Legs (pants)
    draw.rectangle([12, 26, 15, 30], fill=(100, 50, 200))
    draw.rectangle([15, 26, 18, 30], fill=(100, 50, 200))
    
    # Eyes
    draw.ellipse([12, 11, 13, 12], fill=(0, 0, 0))  # Left eye
    draw.ellipse([17, 11, 18, 12], fill=(0, 0, 0))  # Right eye
    
    # Mouth (smile)
    draw.arc([12, 13, 18, 17], start=0, end=180, fill=(0, 0, 0), width=1)
    
    return img

def create_wall_tile():
    """Create a wall tile - 20x20 pixels"""
    img = Image.new('RGBA', (20, 20), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Brick pattern
    # Background (mortar)
    draw.rectangle([0, 0, 20, 20], fill=(120, 120, 120))
    
    # Bricks (reddish-brown)
    draw.rectangle([1, 1, 19, 9], fill=(150, 75, 0))
    draw.rectangle([1, 11, 9, 19], fill=(150, 75, 0))
    draw.rectangle([11, 11, 19, 19], fill=(150, 75, 0))
    
    # Brick lines
    draw.line([0, 10, 20, 10], fill=(100, 100, 100), width=1)
    draw.line([10, 10, 10, 20], fill=(100, 100, 100), width=1)
    
    return img

def create_corridor_tile():
    """Create a corridor floor tile - 20x20 pixels"""
    img = Image.new('RGBA', (20, 20), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Light gray floor
    draw.rectangle([0, 0, 20, 20], fill=(220, 220, 220))
    
    # Add some texture/detail
    for i in range(0, 20, 4):
        draw.line([i, 0, i, 20], fill=(200, 200, 200), width=1)
        draw.line([0, i, 20, i], fill=(200, 200, 200), width=1)
    
    return img

def create_room_tile():
    """Create a room floor tile - 20x20 pixels"""
    img = Image.new('RGBA', (20, 20), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Darker gray floor
    draw.rectangle([0, 0, 20, 20], fill=(180, 180, 180))
    
    # Add some texture/detail
    for i in range(0, 20, 4):
        draw.line([i, 0, i, 20], fill=(160, 160, 160), width=1)
        draw.line([0, i, 20, i], fill=(160, 160, 160), width=1)
    
    return img

def main():
    """Create all sprites and save them to the drawable folder"""
    # Create drawable directory if it doesn't exist
    drawable_dir = "app/src/main/res/drawable"
    os.makedirs(drawable_dir, exist_ok=True)
    
    print("Creating sprites...")
    
    # Create and save sprites
    sprites = [
        ("sprite_player.png", create_player_sprite()),
        ("sprite_teacher.png", create_teacher_sprite()),
        ("sprite_friend.png", create_friend_sprite()),
        ("tile_wall.png", create_wall_tile()),
        ("tile_corridor.png", create_corridor_tile()),
        ("tile_room.png", create_room_tile()),
    ]
    
    for filename, img in sprites:
        filepath = os.path.join(drawable_dir, filename)
        img.save(filepath, "PNG")
        print(f"Created: {filepath}")
    
    print("\nAll sprites created successfully!")
    print("You can now rebuild the project to see the new sprites in the game.")
    print("Note: These are simple placeholder sprites. You can replace them with")
    print("higher-quality sprites from OpenGameArt.org later if desired.")

if __name__ == "__main__":
    main()

