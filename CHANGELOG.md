# Nomad Books - Changelog

### Nomad Books 1.0.1 - 1.16.5
- Replaced Nomad Books in the stronghold library loot pool with 0 to 3 grass pages
- Nomad Books obtained in loot pools (starter chest and previously stronghold librairies) will now also be 3x1x3 instead of the previous 7x3x7

### Nomad Books 1.0 - 1.16.5
- Reduced the starting size of the nomad books from 7x3x7 to 3x1x3, this change serving multiple purposes:
    - This acts as a nerf by bringing down the utility level of the nomad book in the early game, as it was too much of a powerful tool too early
    - This makes the itinerant ink more relevant, as the previous 7 width rarely made the user feel they needed more horizontal space
    - This allows users for more versatility in choosing how big they want their nomad book to be, some users requiring less space than the previous 7x3x7 minimum allowed them
    - This also brings the crafting result more in line with the actual recipe, being a campfire and three pages
<<<<<<< Updated upstream
- In exchange for that nerf, the height upgrade is no longer limited to the width of the books, so it stays in line with the idea of giving more versatility and choice to the player
- Removed the special texture and name of the master nomad book to avoid players being confused on its obtainability
- Grass Pages now have a 50% chance of generating in structure chests, instead of the previous 100%
- Nomad Books now have a 50% chance of generating in stronghold library chests, instead of the previous 100%
- Upgrading the height of a Nomad Book with grass pages now requires the camp to be undeployed
- Removed Vanguard

### Nomad Books 0.5.9 - 1.16.5
- Fixed an issue that would cause a server crash on startup

### Nomad Books 0.5.8 - 1.16.5
- Fixed an issue where only one nomad book would be craftable per Minecraft instance
- Added Vanguard auto-updater

### Nomad Books 0.5.7 - 1.16.3
- Fixed an issue that made inked nomad books unable to progress
- Added a sound effect when an inked nomad book reaches its goal

### Nomad Books 0.5.6 - 1.16.3
- Updated to Minecraft 1.16.3

### Nomad Books 0.5.5 - 1.16.2
- Fixed an issue where ghost camps would visually stay for other clients when retrieving a camp
- Fixed an issue where furnaces would drop experience despite keeping it when redeployed
- Added a nomad book to the creative inventory (master nomad book) that has all upgrades and a width and height of 15 by default
- Updated to Minecraft 1.16.2

### Nomad Books 0.5.4 - 1.16.1
- Fixed an issue where double horizontal blocks such as beds would drop as items

### Nomad Books 0.5.3 - 1.16.1
- Removed block breaking effects when reclaiming a camp
- Fixed an issue where the server would crash upon a player retrieving their camp

### Nomad Books 0.5.2 - 1.16.1
- Changed firefly camp limit render to use custom particles
- Reintroduced the specific error message when trying to retrieve a camp in a different dimension
- Teleporting to a camp using ender pearls will now cost one pearl for every 60 blocks instead of one for any distance
- Fixed an issue where players would get kicked from the server upon applying itinerant ink
- Fixed an issue where some blocks would drop items upon retrieving the camp
- Fixed an issue where fluids would stay upon retrieving a camp
- Fixed an issue where teleporting back to a camp in creative mode would consume an ender pearl
- Fixed an issue where beds would still be counted as occupied when redeploying a camp

### Nomad Books 0.5.1 - 1.16.1
- Fixed an issue where players would get instantly kicked from the server upon joining

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
