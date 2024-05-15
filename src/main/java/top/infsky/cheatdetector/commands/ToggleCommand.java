package top.infsky.cheatdetector.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import top.infsky.cheatdetector.CheatDetector;
import top.infsky.cheatdetector.impl.Check;
import top.infsky.cheatdetector.impl.Module;
import top.infsky.cheatdetector.utils.LogUtils;
import top.infsky.cheatdetector.utils.TRSelf;

public class ToggleCommand {
    public static int execute(@NotNull CommandContext<FabricClientCommandSource> context) {
        try {
            String settingName = getSettingName(context.getArgument("moduleName", String.class));
            CheatDetector.CONFIG_HANDLER.configManager.setValue(settingName,
                    !CheatDetector.CONFIG_HANDLER.configManager.getConfig(boolean.class, settingName)
                            .orElseThrow(() -> new IllegalArgumentException("不正确的设置名: " + settingName)));
            return 1;
        } catch (IllegalArgumentException e) {
            LogUtils.custom(ChatFormatting.DARK_RED + e.getMessage());
            return -1;
        }
    }

    private static @NotNull String getSettingName(String input) throws IllegalArgumentException {
        for (Check check : TRSelf.getInstance().manager.getChecks().values()) {
            if (check instanceof Module module) {
                String moduleName = module.checkName;
                if (!moduleName.equalsIgnoreCase(input)) continue;

                return moduleName.substring(0, 1).toLowerCase() +
                        moduleName.substring(1) +
                        "Enabled";
            }
        }
        throw new IllegalArgumentException("不正确的模块茗: " + input);
    }
}
