package autumnvn.autumn;

import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Options {
    File file;
    Map<String, SimpleOption<?>> options;
    public FreeCam freeCamEntity;

    public SimpleOption<Boolean> autoAttack;
    public SimpleOption<Boolean> ignorePlayer;
    public SimpleOption<Boolean> autoHitSwap;
    public SimpleOption<Boolean> autoSprint;
    public SimpleOption<Boolean> betterChat;
    public SimpleOption<Boolean> betterNametag;
    public SimpleOption<Boolean> deathCoord;
    public SimpleOption<Boolean> freeCam;
    public SimpleOption<Boolean> fullBright;
    public SimpleOption<Boolean> horseSwim;
    public SimpleOption<Boolean> infoHud;
    public SimpleOption<Boolean> instantSneak;
    public SimpleOption<Boolean> keepMiningWhenSwap;
    public SimpleOption<Boolean> noFade;
    public SimpleOption<Boolean> noFishingBobber;
    public SimpleOption<Boolean> noFog;
    public SimpleOption<Boolean> noInvisible;
    public SimpleOption<Boolean> noMineDelay;
    public SimpleOption<Boolean> noToast;
    public SimpleOption<Boolean> noUseDelay;
    public SimpleOption<Boolean> pingNumber;
    public SimpleOption<Boolean> rightClickHarvest;
    public SimpleOption<Boolean> thirdPersonNoClip;
    public SimpleOption<Boolean> visibleBarrier;

    public Options() {
        this.file = new File(AutumnClient.client.runDirectory, "config/autumn.properties");
        this.options = new HashMap<>();

        autoAttack = SimpleOption.ofBoolean("Auto Attack", value -> Tooltip.of(Text.of("Automatically attack living entity at crosshair within reach")), false);
        options.put("autoAttack", autoAttack);
        ignorePlayer = SimpleOption.ofBoolean("Ignore Player", value -> Tooltip.of(Text.of("Auto Attack will ignore player")), false);
        options.put("ignorePlayer", ignorePlayer);
        autoHitSwap = SimpleOption.ofBoolean("Auto Hit Swap", value -> Tooltip.of(Text.of("Swap item in main hand to another item in hotbar when attacking, then swap back after attack so it uses base damage & cooldown of original item and enchantments & ability to disable shield of swapped item\nPriority: axe when target is using shield, enchanted sword")), true);
        options.put("autoHitSwap", autoHitSwap);
        autoSprint = SimpleOption.ofBoolean("Auto Sprint", value -> Tooltip.of(Text.of("Automatically sprint when moving forward")), true);
        options.put("autoSprint", autoSprint);
        betterChat = SimpleOption.ofBoolean("Better Chat", value -> Tooltip.of(Text.of("Lengthen chat history to 65k lines, keep chat/command history on switching world/server & remove chat indicator")), true);
        options.put("betterChat", betterChat);
        betterNametag = SimpleOption.ofBoolean("Better Nametag", value -> Tooltip.of(Text.of("Add health & gamemode to nametag, make player nametag always visible & show recently targeted entity nametag")), true);
        options.put("betterNametag", betterNametag);
        deathCoord = SimpleOption.ofBoolean("Death Coord", value -> Tooltip.of(Text.of("Show death coordinates in chat")), true);
        options.put("deathCoord", deathCoord);
        freeCam = SimpleOption.ofBoolean("Free Cam", value -> Tooltip.of(Text.of("Unbind camera from player, allow fly around & clip through blocks")), false, this::freeCamCallback);
        options.put("freeCam", freeCam);
        fullBright = SimpleOption.ofBoolean("Full Bright", value -> Tooltip.of(Text.of("No more darkness")), true);
        options.put("fullBright", fullBright);
        horseSwim = SimpleOption.ofBoolean("Horse Swim", value -> Tooltip.of(Text.of("Make riding horse swim in water & lava")), true);
        options.put("horseSwim", horseSwim);
        infoHud = SimpleOption.ofBoolean("Info Hud", value -> Tooltip.of(Text.of("Show fps, coordinates, direction, tps, targeted entity health & horse stats on screen, show armor above hotbar, show hunger & xp bar when riding, show status effect amplifier & duration")), true);
        options.put("infoHud", infoHud);
        instantSneak = SimpleOption.ofBoolean("Instant Sneak", value -> Tooltip.of(Text.of("Instantly sneak when holding shift, no animation")), true);
        options.put("instantSneak", instantSneak);
        keepMiningWhenSwap = SimpleOption.ofBoolean("Keep Mining When Swap", value -> Tooltip.of(Text.of("Keep mining block when swapping item")), true);
        options.put("keepMiningWhenSwap", keepMiningWhenSwap);
        noFade = SimpleOption.ofBoolean("No Fade", value -> Tooltip.of(Text.of("Remove fade animation on splash & title screen")), true);
        options.put("noFade", noFade);
        noFishingBobber = SimpleOption.ofBoolean("No Fishing Bobber", value -> Tooltip.of(Text.of("Remove fishing bobber when it hooked on your face")), true);
        options.put("noFishingBobber", noFishingBobber);
        noFog = SimpleOption.ofBoolean("No Fog", value -> Tooltip.of(Text.of("Remove fog, including submersions & potion effects")), true);
        options.put("noFog", noFog);
        noInvisible = SimpleOption.ofBoolean("No Invisible", value -> Tooltip.of(Text.of("Force render invisible entities")), true);
        options.put("noInvisible", noInvisible);
        noMineDelay = SimpleOption.ofBoolean("No Mine Delay", value -> Tooltip.of(Text.of("Remove 6-tick delay when mining blocks")), true);
        options.put("noMineDelay", noMineDelay);
        noToast = SimpleOption.ofBoolean("No Toast", value -> Tooltip.of(Text.of("Remove all in-game toast")), true);
        options.put("noToast", noToast);
        noUseDelay = SimpleOption.ofBoolean("No Use Delay", value -> Tooltip.of(Text.of("Remove 4-tick delay when using items")), false);
        options.put("noUseDelay", noUseDelay);
        pingNumber = SimpleOption.ofBoolean("Ping Number", value -> Tooltip.of(Text.of("Show ping number on tab list")), true);
        options.put("pingNumber", pingNumber);
        rightClickHarvest = SimpleOption.ofBoolean("Right Click Harvest", value -> Tooltip.of(Text.of("Right click to harvest fully-grown crop & netherwart")), true);
        options.put("rightClickHarvest", rightClickHarvest);
        thirdPersonNoClip = SimpleOption.ofBoolean("Third Person No Clip", value -> Tooltip.of(Text.of("Let third-person camera clip through blocks")), true);
        options.put("thirdPersonNoClip", thirdPersonNoClip);
        visibleBarrier = SimpleOption.ofBoolean("Visible Barrier", value -> Tooltip.of(Text.of("Force render barrier block")), true, value -> AutumnClient.client.worldRenderer.reload());
        options.put("visibleBarrier", visibleBarrier);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                reader.lines().forEach(line -> {
                    String[] split = line.split("=");
                    if (split.length != 2) {
                        Autumn.LOGGER.warn("Invalid line in config file: {}", line);
                        return;
                    }
                    String key = split[0];
                    String value = split[1];
                    SimpleOption<?> option = options.get(key);
                    if (option == null || value.isEmpty()) {
                        Autumn.LOGGER.warn("Invalid option in config file: {}", line);
                    } else {
                        parseOption(option, value);
                    }
                });
            } catch (Exception e) {
                Autumn.LOGGER.error("Failed to read config file", e);
            }
        } else {
            boolean mkdirs = file.getParentFile().mkdirs();
            if (!mkdirs) {
                Autumn.LOGGER.error("Failed to create config directory");
            }
            save();
        }
    }

    <T> void parseOption(SimpleOption<T> option, String value) {
        DataResult<T> result = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
        result.error().ifPresent(e -> Autumn.LOGGER.warn("Failed to parse option: {}", e.message()));
        result.result().ifPresent(option::setValue);
    }

    void save() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (Entry<String, SimpleOption<?>> option : options.entrySet()) {
                writer.println(option.getKey() + "=" + option.getValue().getValue());
            }
        } catch (FileNotFoundException e) {
            Autumn.LOGGER.error("Failed to create config file", e);
        }
    }

    // FreeCam
    void freeCamCallback(Boolean value) {
        AutumnClient.client.chunkCullingEnabled = !value;
        AutumnClient.client.gameRenderer.setRenderHand(!value);
        if (value) {
            freeCamEntity = new FreeCam();
            freeCamEntity.spawn();
            AutumnClient.client.setCameraEntity(freeCamEntity);
        } else {
            AutumnClient.client.setCameraEntity(AutumnClient.client.player);
            freeCamEntity.despawn();
            freeCamEntity.input = new Input();
            freeCamEntity = null;
        }
    }
}
