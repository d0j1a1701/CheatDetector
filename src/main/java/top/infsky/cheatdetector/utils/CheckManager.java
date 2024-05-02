package top.infsky.cheatdetector.utils;

import lombok.Getter;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.infsky.cheatdetector.CheatDetector;
import top.infsky.cheatdetector.impl.Check;
import top.infsky.cheatdetector.impl.checks.*;
import top.infsky.cheatdetector.impl.fixes.watchdog.NoSlowDisabler;
import top.infsky.cheatdetector.impl.modules.*;
import top.infsky.cheatdetector.impl.fixes.vulcan.*;
import top.infsky.cheatdetector.impl.fixes.themis.*;
import top.infsky.cheatdetector.impl.modules.common.*;
import top.infsky.cheatdetector.impl.modules.hypixel.Killaura;
import top.infsky.cheatdetector.impl.modules.hypixel.Velocity;
import top.infsky.cheatdetector.impl.modules.pas.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class CheckManager {
    private final TRPlayer player;
    private final @NotNull Map<Class<? extends Check>, Check> checks;
    private final @NotNull Map<Class<? extends Check>, Check> preChecks;
    private final @NotNull Map<Class<? extends Check>, Check> normalChecks;
    private final @NotNull Map<Class<? extends Check>, Check> postChecks;
    public short disableTick;
    public CheckManager(@NotNull Map<Class<? extends Check>, Check> preChecks,
                        @NotNull Map<Class<? extends Check>, Check> normalChecks,
                        @NotNull Map<Class<? extends Check>, Check> postChecks, TRPlayer player) {
        this.player = player;
        this.preChecks = new HashMap<>(preChecks);
        this.normalChecks = new HashMap<>(normalChecks);
        this.postChecks = new HashMap<>(postChecks);
        this.checks = new HashMap<>();
        this.checks.putAll(this.preChecks);
        this.checks.putAll(this.normalChecks);
        this.checks.putAll(this.postChecks);
        this.disableTick = 10;
    }

    @Contract("_ -> new")
    public static @NotNull CheckManager create(@NotNull TRPlayer player) {
        final Map<Class<? extends Check>, Check> pre = new HashMap<>();
        final Map<Class<? extends Check>, Check> normal = new HashMap<>();
        final Map<Class<? extends Check>, Check> post = new HashMap<>();
        pre.put(GroundSpoofA.class, new GroundSpoofA(player));
        normal.put(FlightA.class, new FlightA(player));
        normal.put(BlinkA.class, new BlinkA(player));
        normal.put(SpeedA.class, new SpeedA(player));
        normal.put(SpeedB.class, new SpeedB(player));
        normal.put(HighJumpA.class, new HighJumpA(player));
        normal.put(NoSlowA.class, new NoSlowA(player));
        normal.put(GameModeA.class, new GameModeA(player));
        normal.put(FlightB.class, new FlightB(player));
        normal.put(VelocityA.class, new VelocityA(player));

        return new CheckManager(pre, normal, post, player);
    }

    public static @NotNull CheckManager createSelf(@NotNull TRSelf player) {
        final Map<Class<? extends Check>, Check> pre = new HashMap<>();
        final Map<Class<? extends Check>, Check> normal = new HashMap<>();
        final Map<Class<? extends Check>, Check> post = new HashMap<>();
        pre.put(GroundSpoofA.class, new GroundSpoofA(player));
        normal.put(FlightA.class, new FlightA(player));
        normal.put(BlinkA.class, new BlinkA(player));
        normal.put(SpeedA.class, new SpeedA(player));
        normal.put(SpeedB.class, new SpeedB(player));
        normal.put(HighJumpA.class, new HighJumpA(player));
        normal.put(NoSlowA.class, new NoSlowA(player));
        normal.put(GameModeA.class, new GameModeA(player));
        normal.put(FlightB.class, new FlightB(player));
        normal.put(VelocityA.class, new VelocityA(player));
        pre.put(BadPacket1.class, new BadPacket1(player));
        pre.put(BadPacket2.class, new BadPacket2(player));
        pre.put(MovementDisabler.class, new MovementDisabler(player));
        post.put(FlagDetector.class, new FlagDetector(player));
        pre.put(FastPlace.class, new FastPlace(player));
        pre.put(OmniSprintDisabler.class, new OmniSprintDisabler(player));
        post.put(Spin.class, new Spin(player));
        post.put(NoRotateSet.class, new NoRotateSet(player));
        post.put(ClickGUI.class, new ClickGUI(player));
        post.put(AntiVanish.class, new AntiVanish(player));
        post.put(Blink.class, new Blink(player));
        post.put(AirWalk.class, new AirWalk(player));
        post.put(AntiFall.class, new AntiFall(player));
        post.put(Fakelag.class, new Fakelag(player));
        post.put(AirPlace.class, new AirPlace(player));
        post.put(InvWalk.class, new InvWalk(player));
        post.put(Backtrack.class, new Backtrack(player));
        post.put(NoteBot.class, new NoteBot(player));
        post.put(SayHacker.class, new SayHacker(player));
        post.put(JumpReset.class, new JumpReset(player));
        post.put(Scaffold.class, new Scaffold(player));
        post.put(Velocity.class, new Velocity(player));
        post.put(Killaura.class, new Killaura(player));
        pre.put(NoSlowDisabler.class, new NoSlowDisabler(player));

        return new CheckManager(pre, normal, post, player);
    }

    public void update() {
        if (disableTick > 0) {
            disableTick--;
            return;
        }
        if (player.currentGameType != player.lastGameType) {
            for (Check check : checks.values()) check._onGameTypeChange();
        }

        if (player.currentGameType == GameType.CREATIVE || player.currentGameType == GameType.SPECTATOR) return;
        if (player.lastOnGround && !player.currentOnGround) onJump();
        if (TRPlayer.CLIENT.mouseHandler.isLeftPressed() && TRPlayer.CLIENT.crosshairPickEntity != null)
            onCustomAction(check -> check._handleAttack(TRPlayer.CLIENT.crosshairPickEntity));

        for (Check check : preChecks.values()) check._onTick();
        for (Check check : normalChecks.values()) check._onTick();
        for (Check check : postChecks.values()) check._onTick();
    }

    public void onTeleport() {
        if (player == null || !CheatDetector.inWorld) return;
        for (Check check : checks.values()) check._onTeleport();
    }

    public void onJump() {
        player.jumping = true;
        for (Check check : checks.values()) check._onJump();
    }

    public void onCustomAction(Consumer<Check> action) {
        if (player == null || !CheatDetector.inWorld) return;
        for (Check check : checks.values()) action.accept(check);
    }
}