# Item Stack Marker (Fabric, Minecraft 1.21.11)

Client-side mod. Every dropped item gets a tall white line through it with
its stack count floating above the line. Works on any server.

## Install the finished .jar
1. Fabric Loader 0.18.x for 1.21.11
2. Fabric API for 1.21.11
3. Put `item-stack-marker-1.0.0.jar` in `.minecraft/mods`

## Get the .jar (pick one)

### A) No installs: GitHub builds it for you
1. Make a free GitHub account, create a new repository.
2. Upload every file from this folder (keep the .github folder).
3. Open the Actions tab, wait for "build" to go green.
4. Open that run, download the artifact `item-stack-marker-jar`, unzip it.

### B) On your computer
1. Install JDK 21 and Gradle 9.x
2. In this folder run:  gradle build
3. The jar is `build/libs/item-stack-marker-1.0.0.jar`
   (ignore the `-sources` jar)

## Settings
Run the game once, then edit `config/itemstackmarker.properties`:
line height/thickness, text size, colors (AARRGGBB hex), see-through-walls, etc.

## If something breaks
- Build says a Fabric API version doesn't exist: change `fabric_api_version`
  in gradle.properties to the newest "+1.21.11" version on Modrinth.
- Builds fine but nothing is drawn: in ItemStackMarkerClient.java change
  `AFTER_ENTITIES` to `BEFORE_ENTITIES`.
