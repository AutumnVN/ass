package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
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

            if (getAxeHotbarSlot(player) != -1 && target instanceof PlayerEntity playerEntity && playerEntity.getActiveItem().isOf(Items.SHIELD)) {
                player.getInventory().selectedSlot = getAxeHotbarSlot(player);
                return;
            }

            if (getSmiteSwordHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && livingEntity.getGroup() == EntityGroup.UNDEAD) {
                player.getInventory().selectedSlot = getSmiteSwordHotbarSlot(player);
                return;
            }

            if (getBaneOfArthropodsSwordHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && livingEntity.getGroup() == EntityGroup.ARTHROPOD) {
                player.getInventory().selectedSlot = getBaneOfArthropodsSwordHotbarSlot(player);
                return;
            }

            if (getImpalingTridentHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && livingEntity.getGroup() == EntityGroup.AQUATIC) {
                player.getInventory().selectedSlot = getImpalingTridentHotbarSlot(player);
                return;
            }

            if (getEnchantedSwordHotbarSlot(player) != -1) {
                player.getInventory().selectedSlot = getEnchantedSwordHotbarSlot(player);
                return;
            }

            if (getNonWeaponHotbarSlot(player) != -1 && ((item instanceof SwordItem && stack.getEnchantments().isEmpty()) || (item instanceof AxeItem && EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack) > 0) || item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem || item instanceof TridentItem)) {
                player.getInventory().selectedSlot = getNonWeaponHotbarSlot(player);
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
    int getSmiteSwordHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof SwordItem && EnchantmentHelper.getLevel(Enchantments.SMITE, stack) > 0) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    int getBaneOfArthropodsSwordHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof SwordItem && EnchantmentHelper.getLevel(Enchantments.BANE_OF_ARTHROPODS, stack) > 0) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    int getImpalingTridentHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item instanceof TridentItem && EnchantmentHelper.getLevel(Enchantments.IMPALING, stack) > 0) {
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

    @Unique
    int getNonWeaponHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (!(item instanceof SwordItem) && !(item instanceof AxeItem) && !(item instanceof PickaxeItem) && !(item instanceof ShovelItem) && !(item instanceof HoeItem) && !(item instanceof TridentItem)) {
                return i;
            }
        }
        return -1;
    }
}
