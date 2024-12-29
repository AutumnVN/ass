package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    // BetterChat
    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100))
    private int addMessage(int original) {
        return AutumnClient.options.betterChat.getValue() ? 65535 : original;
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void clear(CallbackInfo ci) {
        if (AutumnClient.options.betterChat.getValue()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"))
    public MessageIndicator messageIndicator(MessageIndicator original) {
        return AutumnClient.options.betterChat.getValue() ? null : original;
    }

}
