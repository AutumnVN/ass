package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.SettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {

    @Shadow
    private Screen parent;

    @Shadow
    private GameOptions settings;

    // SettingsButton
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        ButtonWidget settingsButton = ButtonWidget.builder(Text.of("Autumn Settings..."), button -> {
            this.client.setScreen(new SettingsScreen(this));
        }).dimensions(this.width / 2 - 155, this.height / 6 + 18, 150, 20).build();
        this.addDrawableChild(settingsButton);
    }

    public OptionsScreenMixin(Screen parent, GameOptions gameOptions) {
        super(Text.translatable("options.title", new Object[0]));
    }
}
