package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HPacket;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.function.Function;

@UtilityClass
public class Utils {
    // Read an ArrayList<T> where the packet encodes: [int count] then [count items]
    public static <T> ArrayList<T> readList(HPacket packet, Function<HPacket, T> elementReader) {
        int count = packet.readInteger();
        int n = Math.max(0, count);
        ArrayList<T> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(elementReader.apply(packet));
        }
        return list;
    }
}
