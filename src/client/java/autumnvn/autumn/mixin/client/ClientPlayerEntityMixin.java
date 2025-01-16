package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Final
    @Shadow
    protected MinecraftClient client;

    // AutoSprint
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovement(CallbackInfo info) {
        if (AutumnClient.options.autoSprint.getValue() && client.player != null) {
            client.player.setSprinting(client.options.forwardKey.isPressed());
        }
    }
}
