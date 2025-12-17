# Sprite Setup Guide - OpenGameArt.org

This guide will help you download and add sprites from OpenGameArt.org to replace the colored rectangles in the game with nicer images.

## Required Sprites

The game needs the following sprite files to be placed in `app/src/main/res/drawable/`:

1. **sprite_player.png** - Character sprite for the player (kid)
2. **sprite_teacher.png** - Character sprite for teachers
3. **sprite_friend.png** - Character sprite for friends to rescue
4. **tile_wall.png** - Tile sprite for walls (will be tiled/repeated)
5. **tile_corridor.png** - Tile sprite for corridor floors
6. **tile_room.png** - Tile sprite for room floors

## Recommended Sprite Sizes

- **Player/Teacher/Friend**: 32x32 to 64x64 pixels (square sprites work best)
- **Wall tiles**: 20x20 to 40x40 pixels (will be tiled)
- **Floor tiles**: 20x20 to 40x40 pixels (will be tiled)

## Finding Sprites on OpenGameArt.org

### Option 1: Search for Character Sprites

1. Go to https://opengameart.org
2. Use the search function to find:
   - **Character sprites**: Search for "character sprite", "rpg sprite", "kid sprite", "student sprite"
   - **School sprites**: Search for "school", "classroom", "student"
   - **Tile sets**: Search for "tileset", "tile set", "wall tile", "floor tile"

### Option 2: Browse Popular Collections

Here are some recommended sprite packs from OpenGameArt:

#### Character Sprites:
- **LPC (Liberated Pixel Cup) Character Sprites**: 
  - URL: https://lpc.opengameart.org/content/lpc-character-sprites
  - Contains various character sprites that can be used for kids, teachers, and friends
  - License: Usually CC-BY-SA 3.0 or GPL 3.0

- **16x16 RPG Sprites**:
  - Search for "16x16 rpg sprites" on OpenGameArt
  - Good for small, simple character sprites

#### Tile Sets:
- **16x16 Wall Set**: 
  - URL: https://lpc.opengameart.org/content/16x16-wall-set
  - Contains wall tiles

- **School/Indoor Tilesets**:
  - Search for "indoor tileset", "school tileset", "classroom tileset"
  - Look for floor and wall tiles

### Option 3: Use These Direct Links

1. **Character Sprites**:
   - https://opengameart.org/content/lpc-character-sprites
   - https://opengameart.org/content/twelve-16x18-rpg-sprites-plus-base

2. **Tile Sets**:
   - https://opengameart.org/content/open-pixel-platformer-tiles-sprites
   - https://opengameart.org/content/tilesheet

## Downloading Sprites

1. **Navigate to the sprite pack page** on OpenGameArt.org
2. **Check the license** - Make sure it's compatible with your project (CC-BY, CC-BY-SA, GPL, or Public Domain are usually fine)
3. **Download the sprite pack** - Usually there's a "Download" button or link
4. **Extract the files** - Most downloads are ZIP files
5. **Select appropriate sprites** - Choose sprites that match the style you want

## Preparing Sprites

### For Character Sprites (Player, Teacher, Friend):

1. **Choose a sprite** from the downloaded pack
2. **Extract the sprite** - You may need to use an image editor to extract individual sprites from a sprite sheet
3. **Resize if needed** - Recommended sizes:
   - Player: 40x40 pixels
   - Teacher: 40x40 pixels  
   - Friend: 30x30 pixels
4. **Save as PNG** with transparency (alpha channel)

### For Tile Sprites (Walls, Floors):

1. **Choose a tile** from a tileset
2. **Extract the tile** - Use an image editor to extract individual tiles
3. **Resize if needed** - Recommended sizes:
   - Wall tiles: 20x20 pixels
   - Floor tiles: 20x20 pixels
4. **Save as PNG** with transparency

## Adding Sprites to Your Project

1. **Place sprite files** in `app/src/main/res/drawable/`:
   - `sprite_player.png`
   - `sprite_teacher.png`
   - `sprite_friend.png`
   - `tile_wall.png`
   - `tile_corridor.png`
   - `tile_room.png`

2. **Rebuild the project** - The sprites will be automatically loaded by `SpriteManager`

3. **Test the game** - The sprites should now appear instead of colored rectangles

## Using Image Editing Software

If you need to extract sprites from sprite sheets or resize them:

- **Free options**:
  - GIMP (https://www.gimp.org/)
  - Paint.NET (Windows)
  - Piskel (online sprite editor: https://www.piskelapp.com/)
  - Aseprite (paid, but excellent for pixel art)

### Extracting from Sprite Sheets:

1. Open the sprite sheet in your image editor
2. Use the selection tool to select one sprite
3. Copy the selection
4. Create a new image with the sprite dimensions
5. Paste the sprite
6. Save as PNG with transparency

## Fallback Behavior

If sprites are not found, the game will automatically fall back to:
- **Player**: Blue rectangle
- **Teacher**: Red rectangle
- **Friend**: Green rectangle
- **Walls**: Brick pattern
- **Floors**: Colored rectangles

This means the game will work even without sprites, but adding sprites will make it look much better!

## License Notes

**Important**: When using sprites from OpenGameArt.org:

1. **Check the license** for each sprite pack
2. **Give attribution** if required (usually CC-BY or CC-BY-SA licenses)
3. **Include license information** in your game's credits/README
4. **Respect the license terms** - Some licenses may require you to share your game under the same license

Common licenses:
- **CC-BY**: Must credit the author
- **CC-BY-SA**: Must credit and share under same license
- **GPL**: Must share source code and use GPL license
- **Public Domain**: No restrictions

## Tips

1. **Consistent style**: Try to use sprites from the same pack or artist for a consistent look
2. **Size consistency**: Keep character sprites roughly the same size
3. **Transparency**: Make sure sprites have transparent backgrounds (PNG with alpha)
4. **Testing**: Test sprites in-game to ensure they look good at the game's scale

## Troubleshooting

- **Sprites not showing**: Check that files are named exactly as specified and are in the `drawable` folder
- **Sprites too large/small**: Resize them to match recommended dimensions
- **White background**: Make sure sprites have transparent backgrounds (PNG with alpha channel)
- **Build errors**: Ensure sprite files are valid PNG images

## Example Workflow

1. Go to https://opengameart.org
2. Search for "rpg character sprite"
3. Find a pack you like (e.g., LPC Character Sprites)
4. Download and extract
5. Open sprite sheet in GIMP/Paint.NET
6. Extract individual sprites (player, teacher, friend)
7. Resize to recommended dimensions
8. Save as PNG files with the correct names
9. Copy to `app/src/main/res/drawable/`
10. Rebuild and run the game!

Good luck finding great sprites for your game! 🎮✨

