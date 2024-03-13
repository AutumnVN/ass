package autumnvn.autumn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.option.SimpleOption;

public class Options {
    File file;
    Map<String, SimpleOption<?>> options;

    public Options() {
        this.file = new File(AutumnClient.client.runDirectory, "config/autumn.properties");
        this.options = new HashMap<String, SimpleOption<?>>();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                reader.lines().forEach(line -> {
                    String[] split = line.split("=");
                    if (split.length != 2) {
                        Autumn.LOGGER.warn("Invalid line in config file: " + line);
                        return;
                    }
                    String key = split[0];
                    String value = split[1];
                    SimpleOption<?> option = options.get(key);
                    if (option == null || value.isEmpty()) {
                        Autumn.LOGGER.warn("Invalid option in config file: " + line);
                        return;
                    } else {
                        parseOption(option, value);
                    }
                });
            } catch (Exception e) {
                Autumn.LOGGER.error("Failed to read config file", e);
            }
        } else {
            file.getParentFile().mkdirs();
            save();
        }
    }

    <T> void parseOption(SimpleOption<T> option, String value) {
        DataResult<T> result = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
        result.error().ifPresent(e -> Autumn.LOGGER.warn("Failed to parse option: " + e.message()));
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
}
