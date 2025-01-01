package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    // NoMineDelay
    @ModifyConstant(method = "updateBlockBreakingProgress", constant = @Constant(intValue = 5))
    private int blockBreakingCooldown(int original) {
        return AutumnClient.options.noMineDelay.getValue() ? 0 : original;
    }

    @Shadow
    private ItemStack selectedStack;

    // KeepMiningWhenSwap
    @ModifyVariable(method = "isCurrentlyBreaking", at = @At("STORE"))
    private ItemStack itemStack(ItemStack original) {
        return AutumnClient.options.keepMiningWhenSwap.getValue() ? this.selectedStack : original;
    }

    // FreeCam
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(client.player)) {
            ci.cancel();
        }
    }

    @Unique
    int slot;

    // AutoHitSwap
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void attackEntity2(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (AutumnClient.options.autoHitSwap.getValue()) {
            slot = player.getInventory().selectedSlot;
            ItemStack stack = player.getMainHandStack();
            Item item = stack.getItem();
            boolean targetIsUsingShield = target instanceof PlayerEntity playerEntity && playerEntity.getActiveItem().isOf(Items.SHIELD);
            if (!(item instanceof AxeItem) && targetIsUsingShield && getAxeHotbarSlot(player) != -1) {
                player.getInventory().selectedSlot = getAxeHotbarSlot(player);
            } else if (!(item instanceof AxeItem) || !targetIsUsingShield) {
                if (!(item instanceof SwordItem && !stack.getEnchantments().isEmpty()) && getEnchantedSwordHotbarSlot(player) != -1) {
                    player.getInventory().selectedSlot = getEnchantedSwordHotbarSlot(player);
                }
            }
        }
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void attackEntity3(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (AutumnClient.options.autoHitSwap.getValue()) {
            player.getInventory().selectedSlot = slot;
        }
    }

    @Unique
    int getAxeHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    int getEnchantedSwordHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof SwordItem && !stack.getEnchantments().isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
