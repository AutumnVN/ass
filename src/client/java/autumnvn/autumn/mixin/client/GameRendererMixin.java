package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Final
    @Shadow
    MinecraftClient client;

    @Unique
    private Double defaultMouseSen;

    // Zoom
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        SimpleOption<Double> mouseSen = client.options.getMouseSensitivity();
        if (AutumnClient.zoomKey.isPressed()) {
            mouseSen.setValue(defaultMouseSen / 4);
            cir.setReturnValue(cir.getReturnValueD() / 4);
        } else if (defaultMouseSen == null) {
            defaultMouseSen = mouseSen.getValue();
        } else {
            mouseSen.setValue(defaultMouseSen);
        }
    }
}
