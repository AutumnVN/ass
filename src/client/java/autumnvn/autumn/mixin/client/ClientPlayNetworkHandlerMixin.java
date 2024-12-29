package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Unique
    private long lastTime;

    @Unique
    private long lastTick;

    // TPS
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        long tick = packet.getTime();
        long time = System.nanoTime();
        if (lastTick != 0 && lastTime != 0) {
            long passedTick = tick - lastTick;
            long passedTime = time - lastTime;
            if (passedTick > 0 && passedTime > 0) {
                long mspt = passedTime / passedTick / 1000000;
                if (mspt > 0) {
                    AutumnClient.tps = Math.min(1000 / mspt, 20);
                }
            }
        }
        lastTick = tick;
        lastTime = time;
    }

    // FreeCam
    @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
    private void onPlayerRespawn(CallbackInfo ci) {
        AutumnClient.options.freeCam.setValue(false);
    }
}
