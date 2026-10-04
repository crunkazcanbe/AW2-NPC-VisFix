# AW2 NPC Vis Fix

Fixes **Ancient Warfare 2** faction NPCs rendering **invisible** in big **Minecraft 1.12.2** packs (body gone, only the
hitbox and nametag left). It hooks AW2's NPC renderer, resets the GL state and temporarily clears the invisible flag
so NPC bodies draw again, and logs what it saw (`[AW2VisFix]`) to help track down the clashing mod.
Only AW2 NPCs are touched.

See **[Releases](../../releases)**. MIT licensed.


## Compile-only jars

The build compiles against these jars in `libs/` (other authors' mods / APIs). They are not included in this repo — get them from their official pages and drop them in `libs/` before building:

- `aw2.jar`
- `mixinbooter-api.jar`
