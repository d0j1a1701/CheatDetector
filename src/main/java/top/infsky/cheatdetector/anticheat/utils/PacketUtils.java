package top.infsky.cheatdetector.anticheat.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

import java.util.List;

public class PacketUtils {
    public static class Backtrack {
        public static final List<Class<? extends Packet<?>>> INCLUDE_PACKETS = List.of(
                ClientboundPingPacket.class,
                ClientboundPlayerAbilitiesPacket.class,
                ClientboundKeepAlivePacket.class,
                ClientboundMoveEntityPacket.class,
                ClientboundPlayerPositionPacket.class,
                ClientboundPlayerLookAtPacket.class,
                ClientboundDamageEventPacket.class,
                ClientboundAnimatePacket.class,
                ClientboundHurtAnimationPacket.class,
                ClientboundSetEntityMotionPacket.class
        );
        public static final List<Class<? extends Packet<?>>> DELAY_PACKETS = List.of(
                ClientboundPingPacket.class,
                ClientboundPlayerAbilitiesPacket.class,
                ClientboundKeepAlivePacket.class,
                ClientboundMoveEntityPacket.class,
                ClientboundPlayerPositionPacket.class,
                ClientboundPlayerLookAtPacket.class,
                ClientboundSetEntityMotionPacket.class
        );
    }
}