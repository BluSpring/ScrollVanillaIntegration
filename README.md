# ScrollVanillaIntegration

A simple mod that just integrates [DockyardMC's Scroll library](https://github.com/DockyardMC/Scroll) within base Minecraft.

This can probably be JiJ'd very easily to make life easier.

## Usage
Gradle
```gradle
repositories {
    maven {
        name = "devOS"
        url = uri("https://mvn.devos.one/releases")
    }
}

dependencies {
    include(implementation("xyz.bluspring:ScrollVanillaIntegration:1.0.0")!!)
}
```