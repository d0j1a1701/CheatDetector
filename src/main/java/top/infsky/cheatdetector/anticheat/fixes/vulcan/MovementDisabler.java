package top.infsky.cheatdetector.anticheat.fixes.vulcan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import top.infsky.cheatdetector.anticheat.Check;
import top.infsky.cheatdetector.anticheat.TRPlayer;
import top.infsky.cheatdetector.anticheat.TRSelf;
import top.infsky.cheatdetector.anticheat.modules.ClickGUI;
import top.infsky.cheatdetector.config.Advanced2Config;
import top.infsky.cheatdetector.config.FixesConfig;

public class MovementDisabler extends Check {
    public short waitTicks = -1;
    public boolean lastEnabled;  // 上个tick启用状态

    public MovementDisabler(@NotNull TRSelf player) {
        super("MovementDisabler", player);
        lastEnabled = FixesConfig.vulcanDisablerEnabled;
    }

    @Override
    public void _onTick() {
        // tick before
        if (isDisabled()) {
            if (lastEnabled)
                moduleMsg(ChatFormatting.DARK_RED + "已禁用");
            lastEnabled = false;
            return;
        }
        if (TRPlayer.CLIENT.player == null || TRPlayer.CLIENT.getConnection() == null) { lastEnabled = true; return; }

        // on tick
        // copy from liquid-bounce nextgen :D
        if (checkItem()) {
            if (!lastEnabled) moduleMsg(ChatFormatting.AQUA + "已启用，一秒后生效。");

            if (waitTicks == 0) {
                final Entity entity = TRPlayer.CLIENT.player;
                TRPlayer.CLIENT.getConnection().send(
                        ServerboundInteractPacket.createInteractionPacket(entity, false, InteractionHand.OFF_HAND)
                );
                TRPlayer.CLIENT.getConnection().send(
                        new ServerboundPlayerActionPacket(
                                ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN
                        )
                );

                if (Advanced2Config.movementShowPacketSend)
                    moduleMsg(ChatFormatting.GRAY + "send packet.");
            }
            waitTicks = 20;
        } else if (!lastEnabled) {  // 玩家刚刚启用这个选项
            FixesConfig.vulcanDisablerEnabled = false;
            ClickGUI.update();
            moduleMsg(ChatFormatting.RED + "模块不可用，请检查是否满足要求。");
        }

        // after tick
        if (waitTicks >= 0) waitTicks--;
        lastEnabled = FixesConfig.vulcanDisablerEnabled;
    }

    private boolean checkItem() {
        final ItemStack offhand = player.fabricPlayer.getOffhandItem();
        return offhand.getItem() == Items.TRIDENT && offhand.isEnchanted()
                && EnchantmentHelper.getEnchantments(offhand).containsKey(Enchantments.RIPTIDE);
    }

    @Override
    public boolean isDisabled() {
        return !FixesConfig.vulcanDisablerEnabled;
    }
}