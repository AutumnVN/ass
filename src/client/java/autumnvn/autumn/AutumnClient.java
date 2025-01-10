package autumnvn.autumn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class AutumnClient implements ClientModInitializer {
    public static MinecraftClient client;
    public static Options options;
    public static KeyBinding autoAttackKey;
    public static KeyBinding ignorePlayerKey;
    public static KeyBinding freeCamKey;
    public static KeyBinding settingKey;
    public static KeyBinding zoomKey;
    public static double tps;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        options = new Options();
        autoAttackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Attack", GLFW.GLFW_KEY_R, "Autumn"));
        ignorePlayerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Ignore Player", GLFW.GLFW_KEY_UNKNOWN, "Autumn"));
        freeCamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Free Cam", GLFW.GLFW_KEY_H, "Autumn"));
        settingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, "Autumn"));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Zoom", GLFW.GLFW_KEY_LEFT_ALT, "Autumn"));

        FabricLoader.getInstance().getModContainer("autumn").ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("autumn", "autumn"), container, ResourcePackActivationType.DEFAULT_ENABLED));
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.BARRIER, RenderLayer.getTranslucent());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            options.autoAttack.setValue(false);
            options.freeCam.setValue(false);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null || client.world == null) return;
            while (settingKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }
            handleToggleKey(autoAttackKey, options.autoAttack, "Auto Attack");
            handleToggleKey(ignorePlayerKey, options.ignorePlayer, "Ignore Player");
            handleToggleKey(freeCamKey, options.freeCam, "Free Cam");

            // AutoAttack
            if (options.autoAttack.getValue() && client.player.getAttackCooldownProgress(0) >= 1) {
                if (client.targetedEntity instanceof LivingEntity livingEntity && livingEntity.isAttackable() && livingEntity.isAlive() && livingEntity.hurtTime == 0 && !(options.ignorePlayer.getValue() && livingEntity instanceof PlayerEntity)) {
                    client.interactionManager.attackEntity(client.player, livingEntity);
                    client.player.swingHand(client.player.getActiveHand());
                } else if (client.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof InteractionEntity interactionEntity) {
                    NbtCompound nbt = interactionEntity.writeNbt(new NbtCompound());
                    if (nbt.getFloat("width") < 2 && nbt.getFloat("height") > 1.6) {
                        client.interactionManager.attackEntity(client.player, interactionEntity);
                        client.player.swingHand(client.player.getActiveHand());
                    }
                }
            }

            // FreeCam
            if (AutumnClient.options.freeCam.getValue() && client.player.input instanceof KeyboardInput) {
                Input input = new Input();
                input.sneaking = client.player.input.sneaking;
                client.player.input = input;
            } else if (!AutumnClient.options.freeCam.getValue() && !(client.player.input instanceof KeyboardInput)) {
                client.player.input = new KeyboardInput(client.options);
            }

            // RightClickHarvest
            if (options.rightClickHarvest.getValue() && client.options.useKey.isPressed() && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
                BlockPos pos = hitResult.getBlockPos();
                Block block = client.world.getBlockState(pos).getBlock();
                if (block instanceof CropBlock cropBlock && cropBlock.isMature(client.world.getBlockState(pos)) || block instanceof NetherWartBlock && client.world.getBlockState(pos).get(NetherWartBlock.AGE) == 3) {
                    client.interactionManager.attackBlock(pos, hitResult.getSide());
                    client.player.swingHand(client.player.getActiveHand());
                }
            }
        });
    }

    static void handleToggleKey(KeyBinding key, SimpleOption<Boolean> option, String name) {
        if (client.player == null) return;
        while (key.wasPressed()) {
            option.setValue(!option.getValue());
            client.player.sendMessage(Text.of(name + " is now " + (option.getValue() ? "§aON" : "§cOFF")), true);
        }
    }
}
