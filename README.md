# G-Packets

G-Packets turns Habbo packets into Java classes for your G-Earth extensions. Intercept a packet and get a `Users` or `Chat` object with named fields instead of raw bytes, change it, or build your own and send it.

Documentation: https://roboroads.github.io/G-Packets/

## Installation

G-Packets is published through JitPack and needs Java 8 or later.

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

## Example

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

The [packet reference](https://roboroads.github.io/G-Packets/packets/) lists every implemented packet and its parameters.

## Documentation

The full documentation covers:

- intercepting packets with annotations, the `TYPE` descriptor or raw G-Earth listeners
- changing, blocking, building and sending packets
- reading and writing any packet as named values, for packet inspectors and editors
- JSON
- adding a packet

It is built from the `docs/` folder in this repository.

## Contributing

Missing a packet? See [Contributing a packet](https://roboroads.github.io/G-Packets/contributing/), or open a packet request issue.
