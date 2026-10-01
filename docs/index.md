# G-Packets

G-Packets turns Habbo packets into Java classes for your G-Earth extensions. The server sends raw bytes with no names attached; G-Packets knows each packet's layout, so you get a `Users` or a `Chat` object with named fields. You may read it, change it, or build your own and send it.

Not every packet is implemented yet. The [packet reference](packets/index.md) lists the ones that are.

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

## What you can do

- [Install it](getting-started.md) through JitPack and write your first handler.
- [Intercept packets](intercepting.md) with annotations, the `TYPE` descriptor, or raw G-Earth listeners.
- [Change, block, build and send packets](changing-and-sending.md).
- [Read and write any packet as named values](parameters.md), for packet loggers, inspectors and editors.
- [Convert packets to and from JSON](json.md).
- [Add a packet](contributing.md) that G-Packets doesn't have yet.
