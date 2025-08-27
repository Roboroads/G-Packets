package me.roboroads.gearth.gpackets.support;

import gearth.protocol.HPacket;

public interface SubPacket {
    void appendPacket(HPacket packet);
}
