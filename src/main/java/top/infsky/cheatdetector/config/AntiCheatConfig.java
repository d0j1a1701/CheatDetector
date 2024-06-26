package top.infsky.cheatdetector.config;

import top.hendrixshen.magiclib.malilib.api.annotation.Config;
import top.hendrixshen.magiclib.malilib.api.annotation.Hotkey;
import top.hendrixshen.magiclib.malilib.api.annotation.Numeric;
import top.infsky.cheatdetector.config.utils.ConfigCategory;

public class AntiCheatConfig {
    @Hotkey
    @Config(category = ConfigCategory.ANTICHEAT)
    public static boolean antiCheatEnabled = true;

    @Hotkey
    @Config(category = ConfigCategory.ANTICHEAT)
    public static boolean disableSelfCheck = false;

    @Hotkey
    @Config(category = ConfigCategory.ANTICHEAT)
    public static boolean falseFlagFix = false;

    @Numeric(minValue = 0.0, maxValue = Double.MAX_VALUE)
    @Config(category = ConfigCategory.ANTICHEAT)
    public static double threshold = 1.0;

    @Numeric(minValue = -1, maxValue = Integer.MAX_VALUE)
    @Config(category = ConfigCategory.ANTICHEAT)
    public static int VLClearTime = 6000;
}
