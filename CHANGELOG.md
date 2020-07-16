# Nomad Books - Changelog

### Nomad Books 0.5.2 - 1.16.1
- Changed firefly camp limit render to use custom particles
- Fixed an issue where players would get kick from a server upon applying itinerant ink
- Fixed an issue where some blocks would drop items upon retrieving the camp
- Fixed an issue where fluids would stay upon retrieving a camp
- Fixed an issue where teleporting back to a camp in creative mode would consume an ender pearl
- Reintroduced the specific error message when trying to retrieve a camp in a different dimension

### Nomad Books 0.5.1 - 1.16.1
- Fixed an issue where players would get instantly kicked from a server upon joining

### Nomad Books 0.5 - 1.16.1
- Updated to Minecraft 1.16.1
- Dimension will no longer be shown in the tooltip of a deployed camp
- Coordinates are now obfuscated when the camp is in a different dimension than the player
- Removed the nomad page from the creative inventory
- Removed nomad books being able to have multiple times the same upgrade
- Added the nether nomad book, a fireproof upgrade just like the netherite items, obtainable by combining a nomad book and a netherite ingot

### Nomad Books 0.4.2 - 20w17a
- Updated to Minecraft snapshot 20w17a

### Nomad Books 0.4.1 - 20w13b
- Updated to Minecraft snapshot 20w13b

### Nomad Books 0.4 - 20w09a
- Player now needs to be in a 10 block radius from the camp to be able to retrieve it
- Players can now use a nomad book with an ender pearl in their off hand to teleport to their deployed camp (when far enough from the camp)
- Grass pages now appear in burried treasure chests and pillager outposts
- A guaranteed nomad book now appears in bonus chests
- Simplified the crafting recipe for the nomad book: it is now 3 grass pages and a campfire
- Removed the crafting recipe for the nomad page so it is no longer obtainable in survival
- Updated to Minecraft snapshot 20w09a

### Nomad Books 0.3 - 20w06a
- Removed console spam when crafting or upgrading nomad books
- Updated to Minecraft snapshot 20w06a

### Nomad Books 0.2.1 - 1.15.1
- All plants (flowers, lily-pads, vines...) will now be destroyed when deploying a camp
- Make nomad book and nomad page recipes appear in REI
- Fixed a bug where the fungi support mushroom stem would destroy terrain
- Fixed a bug that made newly crafted nomad books and pages have a width of 0 instead of 7

### Nomad Books 0.2 - 1.15.1
- Introduced special book upgrades, obtained by combining special pages with your nomad book, that trigger additional effects upon setting up your camp:
  - Introduced "Aquatic Membrane", obtained from the aquatic membrane page
  - Introduced "Fungi Support", obtained from the mycelium page
- Introduced width upgrades (books and pages start with a width of 7), obtainable via itinerant ink
- Introduced the itinerant ink
- Displaying camp boundaries is now toggle-able (sneak and use) and no longer requires to keep using the book while sneaking
- Camp boundaries are now displayed on the edge of the included blocks
- Limited maximum height to the book's width
- Each cartographer villager chest will now have a guaranteed grass page
- Fixed the "No class found" error upon launching the game and mod without Fabric API installed
- Added Dutch and German localization (thanks to HeresHavi and UpcraftLP!)
