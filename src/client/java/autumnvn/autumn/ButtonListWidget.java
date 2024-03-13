package autumnvn.autumn;

import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.SimpleOption;

public class ButtonListWidget extends ElementListWidget<ButtonListWidget.ButtonEntry> {
    public ButtonListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    void addOptionEntry(SimpleOption<?> options) {
        this.addEntry(ButtonEntry.create(this.width, options));
    }

    void addOptionEntry(SimpleOption<?> firstOption, SimpleOption<?> secondOption) {
        this.addEntry(ButtonEntry.create(this.width, firstOption, secondOption));
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
        List<ClickableWidget> buttons;

        ButtonEntry(Map<SimpleOption<?>, ClickableWidget> buttonMap) {
            this.buttons = ImmutableList.copyOf(buttonMap.values());
        }

        static ButtonEntry create(int width, SimpleOption<?> option) {
            return new ButtonEntry(ImmutableMap.of(option, option.createWidget(AutumnClient.client.options, width / 2 - 155, 0, 150)));
        }

        static ButtonEntry create(int width, SimpleOption<?> firstOption, SimpleOption<?> secondOption) {
            return new ButtonEntry(ImmutableMap.of(firstOption, firstOption.createWidget(AutumnClient.client.options, width / 2 - 155, 0, 150), secondOption, secondOption.createWidget(AutumnClient.client.options, width / 2 + 5, 0, 150)));
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.buttons.forEach(button -> {
                button.setY(y);
                button.render(context, mouseX, mouseY, tickDelta);
            });
        }

        public List<? extends Element> children() {
            return this.buttons;
        }

        public List<? extends Selectable> selectableChildren() {
            return this.buttons;
        }
    }

}
