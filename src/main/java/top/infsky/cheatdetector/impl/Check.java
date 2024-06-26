package top.infsky.cheatdetector.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.infsky.cheatdetector.config.AlertConfig;
import top.infsky.cheatdetector.config.AntiCheatConfig;
import top.infsky.cheatdetector.utils.LogUtils;
import top.infsky.cheatdetector.utils.TRPlayer;
import top.infsky.cheatdetector.utils.TRSelf;

@Getter
public abstract class Check {
    protected final @NotNull TRPlayer player;
    public String checkName;
    public int violations;

    public Check(String checkName, @NotNull TRPlayer player) {
        this.checkName = checkName;
        this.player = player;
    }

    public abstract int getAlertBuffer();
    public abstract boolean isDisabled();

    protected void flag() {
        if (player.manager.disableTick > 0) return;
        if (!AntiCheatConfig.antiCheatEnabled) return;
        if (isDisabled()) return;
        if (AntiCheatConfig.disableSelfCheck && player.equals(TRSelf.getInstance())) return;
        violations++;
        if (!AlertConfig.disableBuffer)
            if (violations % getAlertBuffer() != 0) return;
        LogUtils.alert(player.fabricPlayer.getName().getString(), checkName, String.format("(VL:%s)", violations));
    }

    protected void flag(String extraMsg) {
        if (player.manager.disableTick > 0) return;
        if (!AntiCheatConfig.antiCheatEnabled) return;
        if (isDisabled()) return;
        if (AntiCheatConfig.disableSelfCheck && player.equals(TRSelf.getInstance())) return;
        violations++;
        if (!AlertConfig.disableBuffer)
            if (violations % getAlertBuffer() != 0) return;
        LogUtils.alert(player.fabricPlayer.getName().getString(), checkName, String.format("(VL:%s) %s%s", violations, ChatFormatting.GRAY, extraMsg));
    }

    protected void moduleMsg(String msg) {
        LogUtils.prefix(checkName, msg);
    }

    protected static void customMsg(String msg) {
        LogUtils.custom(msg);
    }

    public void _onTick() {}
    public void _onTeleport() {}
    public void _onJump() {}
    public void _onGameTypeChange() {}
    public <T> boolean _handleStartDestroyBlock(CallbackInfoReturnable<T> cir, T fallbackReturn) { return false; }
    public boolean _handleStopDestroyBlock(CallbackInfo ci) { return false; }
    public boolean _onPacketSend(@NotNull Packet<ServerGamePacketListener> basePacket, Connection connection, PacketSendListener listener, CallbackInfo ci) { return false; }
    public boolean _onPacketReceive(@NotNull Packet<ClientGamePacketListener> basePacket, Connection connection, ChannelHandlerContext channelHandlerContext, CallbackInfo ci) { return false; }
    public void _handleAttack(Entity entity) {
    }
}
