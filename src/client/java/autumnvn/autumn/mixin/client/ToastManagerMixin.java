package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToastManager.class)
public class ToastManagerMixin {

    // NoToast
    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    public void draw(CallbackInfo ci) {
        if (AutumnClient.options.noToast.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void add(CallbackInfo ci) {
        if (AutumnClient.options.noToast.getValue()) {
            ci.cancel();
        }
    }

    @Mixin(targets = "net.minecraft.client.toast.ToastManager$Entry")
    static class Entry {
        @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
        public void draw(int x, DrawContext context, CallbackInfoReturnable<Boolean> cir) {
            if (AutumnClient.options.noToast.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }
}
