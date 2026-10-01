# Getting started

## Introduction

G-Packets is a library that runs inside your G-Earth extension. It needs Java 8 or later, and it doesn't need anything else from you: no code generation, no configuration files.

## Installation

G-Packets is published through JitPack. Add the JitPack repository and the dependency.

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Roboroads</groupId>
        <artifactId>G-Packets</artifactId>
        <version>main-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Roboroads:G-Packets:main-SNAPSHOT'
}
```

!!! note
    `main-SNAPSHOT` follows the main branch, so a rebuild may pick up new changes. For a build that stays the same, use a release tag or a commit hash as the version; JitPack accepts both.

## Your first handler

Here is a complete extension that prints how many users each `Users` packet holds. The server sends one when you enter a room, listing everyone there, and another whenever someone joins:

```java
import gearth.extensions.ExtensionForm;
import me.roboroads.gearth.gpackets.GPackets;
import me.roboroads.gearth.gpackets.Intercept;
import me.roboroads.gearth.gpackets.incoming.Users;

public class YourExtension extends ExtensionForm {
    @Override
    protected void initExtension() {
        GPackets.init(this);
    }

    @Intercept
    void onUsers(Users users) {
        System.out.println("There are " + users.users().size() + " users in this room.");
    }
}
```

`GPackets.init(this)` finds the `@Intercept` method and registers it with G-Earth. Every time the server sends that packet, `onUsers` receives it as a `Users` object, with each user already parsed into a `Player`, `Pet` or `Bot`.

## Finding the packet you need

The [packet reference](packets/index.md) lists every packet G-Packets implements, with its parameters in wire order. [Sulek](https://sulek.dev) explains what Habbo's packets do. If the packet you need is missing, [Contributing a packet](contributing.md) shows how to add it.

## Next steps

- [Intercepting packets](intercepting.md) covers the other ways to intercept, blocking, and handlers in other classes.
- [Changing and sending packets](changing-and-sending.md) shows how to edit a packet or send your own.
