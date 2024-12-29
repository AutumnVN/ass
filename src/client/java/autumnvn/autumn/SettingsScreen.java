package autumnvn.autumn;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {
    Screen parent;
    ButtonListWidget list;
    SimpleOption<?>[] options = new SimpleOption<?>[]{
            AutumnClient.options.autoAttack,
            AutumnClient.options.ignorePlayer,
            AutumnClient.options.betterChat,
            AutumnClient.options.betterNametag,
            AutumnClient.options.deathCoord,
            AutumnClient.options.freeCam,
            AutumnClient.options.fullBright,
            AutumnClient.options.horseSwim,
            AutumnClient.options.infoHud,
            AutumnClient.options.instantSneak,
            AutumnClient.options.keepMiningWhenSwap,
            AutumnClient.options.noFade,
            AutumnClient.options.noFishingBobber,
            AutumnClient.options.noFog,
            AutumnClient.options.noInvisible,
            AutumnClient.options.noMineDelay,
            AutumnClient.options.noToast,
            AutumnClient.options.noUseDelay,
            AutumnClient.options.pingNumber,
            AutumnClient.options.rightClickHarvest,
            AutumnClient.options.thirdPersonNoClip,
            AutumnClient.options.visibleBarrier
    };

    public SettingsScreen(Screen parent) {
        super(Text.of("Autumn Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height - 64, 32, this.height - 32, 25);
        for (int i = 0; i < options.length; i += 2) {
            if (i + 1 < options.length) {
                this.list.addOptionEntry(options[i], options[i + 1]);
            } else {
                this.list.addOptionEntry(options[i]);
            }
        }
        this.addDrawableChild(this.list);

        ButtonWidget doneButton = ButtonWidget.builder(ScreenTexts.DONE, button -> {
            AutumnClient.options.save();
            if (this.client != null) {
                this.client.setScreen(parent);
            }
        }).dimensions(this.width / 2 - 100, this.height - 26, 200, 20).build();
        this.addDrawableChild(doneButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xffffff);
    }

    @Override
    public void removed() {
        AutumnClient.options.save();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
