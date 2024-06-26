package top.infsky.cheatdetector.impl.modules.danger;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.infsky.cheatdetector.config.Advanced3Config;
import top.infsky.cheatdetector.config.DangerConfig;
import top.infsky.cheatdetector.impl.Module;
import top.infsky.cheatdetector.impl.modules.common.Rotation;
import top.infsky.cheatdetector.impl.utils.world.BlockUtils;
import top.infsky.cheatdetector.impl.utils.world.LevelUtils;
import top.infsky.cheatdetector.impl.utils.world.PlayerRotation;
import top.infsky.cheatdetector.utils.TRPlayer;
import top.infsky.cheatdetector.utils.TRSelf;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Nuker extends Module {
    @Getter
    @Nullable
    private static Module instance = null;
    private Block targetBlockType = null;
    private final Queue<BlockPos> cacheBlocks = new LinkedBlockingQueue<>();

    public Nuker(@NotNull TRSelf player) {
        super("Nuker", player);
        instance = this;
    }

    @Override
    public void _onTick() {
        if (isDisabled()) {
            targetBlockType = null;
            cacheBlocks.clear();
            return;
        }

        if (targetBlockType == null) return;
        cacheBlocks.clear();
        Vec3 eyePosition = player.fabricPlayer.getEyePosition();
        Vec3 range = new Vec3(Advanced3Config.nukerRange, Advanced3Config.nukerRange, Advanced3Config.nukerRange);
        BlockUtils.getAllInBox(BlockPos.containing(eyePosition.subtract(range)), BlockPos.containing(eyePosition.add(range)))
                .stream()
                .filter(blockPos -> blockPos.getCenter().distanceTo(eyePosition) <= Advanced3Config.nukerRange)
                .forEach(cacheBlocks::add);

        int hasBreak = 0;
        while (!cacheBlocks.isEmpty() && hasBreak < Advanced3Config.nukerMultiBreak) {
            BlockPos blockPos = null;
            BlockState blockState = null;
            try {
                while (blockState == null || blockState.isAir() || !blockState.is(targetBlockType)
                        || (Advanced3Config.nukerKeepGround && blockPos.equals(player.fabricPlayer.getOnPos()))
                        || (Advanced3Config.nukerYCheck && blockPos.getY() < player.fabricPlayer.getBlockY())) {
                    blockPos = Objects.requireNonNull(cacheBlocks.poll());
                    blockState = LevelUtils.getClientLevel().getBlockState(blockPos);
                }
            } catch (NullPointerException e) {
                return;
            }

            double yaw = player.fabricPlayer.getYRot();
            if (Advanced3Config.nukerDoRotation) {
                if (Advanced3Config.nukerSilentRotation) {
                    Rotation.silentRotate(PlayerRotation.getYaw(blockPos), PlayerRotation.getPitch(blockPos));
                } else {
                    PlayerRotation.rotate(PlayerRotation.getYaw(blockPos), PlayerRotation.getPitch(blockPos));
                }
            }
            if (TRPlayer.CLIENT.gameMode == null) throw new NullPointerException("Player GameMode is null!");


            TRPlayer.CLIENT.gameMode.continueDestroyBlock(blockPos, Direction.fromYRot(yaw));
            player.fabricPlayer.swing(InteractionHand.MAIN_HAND);

            hasBreak++;
        }
    }

    @Override
    public boolean _onPacketSend(@NotNull Packet<ServerGamePacketListener> basepacket, Connection connection, PacketSendListener listener, CallbackInfo ci) {
        if (isDisabled()) return false;

        if (TRPlayer.CLIENT.mouseHandler.isLeftPressed() && basepacket instanceof ServerboundPlayerActionPacket packet) {
            if (packet.getAction() == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
                Block block = LevelUtils.getClientLevel().getBlockState(packet.getPos()).getBlock();
                if (block != targetBlockType) {
                    targetBlockType = block;
                    moduleMsg(Component.translatable("cheatdetector.chat.alert.nukerSetBlock").getString().formatted(targetBlockType.getName().getString()));
                }
            }
        }
        return false;
    }

    @Override
    public boolean isDisabled() {
        return !DangerConfig.nukerEnabled || !DangerConfig.aaaDangerModeEnabled;
    }
}
