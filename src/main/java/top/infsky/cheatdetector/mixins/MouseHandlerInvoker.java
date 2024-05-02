package top.infsky.cheatdetector.mixins;


import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MouseHandler.class)
public interface MouseHandlerInvoker {
    @Accessor("isRightPressed")
    void setRightPressed(boolean value);
}
