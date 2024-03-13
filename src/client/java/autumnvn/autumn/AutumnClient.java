package autumnvn.autumn;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class AutumnClient implements ClientModInitializer {
    public static MinecraftClient client;
    public static Options options;
    public static KeyBinding settingKey;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        options = new Options();
        settingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, "Autumn"));

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            while (settingKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }
        });
    }
}
