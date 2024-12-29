package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Shadow
    protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }

    // BetterNametag
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (AutumnClient.options.betterNametag.getValue() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof PlayerEntity || livingEntity == Utils.getTargetedEntity())) {
            float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
            String ownerName = Utils.getOwnerName(livingEntity);
            renderLabelIfPresent(entity,
                    Text.of(
                            String.format("%s%s %s%.0f§c❤%s",
                                    ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                                    Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                                    Utils.color(health, 0, livingEntity.getMaxHealth()),
                                    health,
                                    livingEntity instanceof PlayerEntity playerEntity ? (playerEntity.isCreative() ? " §r[C]" : playerEntity.isSpectator() ? " §r[S]" : "") : ""
                            )
                    ), matrices, vertexConsumers, light);
            ci.cancel();
        }
    }

    @ModifyConstant(method = "renderLabelIfPresent", constant = @Constant(doubleValue = 4096.0))
    private double maxSquaredDistance(double original) {
        return AutumnClient.options.betterNametag.getValue() ? 128 * 128 : original;
    }

    @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 0))
    private void draw(Args args) {
        if (AutumnClient.options.betterNametag.getValue()) {
            args.set(3, 0xaaffffff);
            args.set(7, TextRenderer.TextLayerType.SEE_THROUGH);
        }
    }
}
