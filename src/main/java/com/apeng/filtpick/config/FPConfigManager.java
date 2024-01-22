package com.apeng.filtpick.config;

import com.apeng.filtpick.config.gson.Adaptable;
import com.apeng.filtpick.config.gson.IllegalConfigStructure;
import com.apeng.filtpick.config.util.WidgetPosOffset;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;

/**
 * Singleton
 */
public final class FPConfigManager {

    private static FPConfigManager INSTANCE;
    private static final int EOF = -1;
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson
            = new GsonBuilder()
            .registerTypeAdapter(WidgetOffsetConfig.class, WidgetOffsetConfig.ofDefault().getTypeAdapter())
            .registerTypeAdapter(WidgetPosOffset.class, WidgetPosOffset.DEFAULT.getTypeAdapter())
            .setPrettyPrinting()
            .create();
    private FileChannel fileChannel;
    private WidgetOffsetConfig widgetOffsetConfig;

    private FPConfigManager(Path configDirPath) {
        initFileChannel(configDirPath);
        if (isFileEmpty()) {
            writeDefaultConfig();
        } else {
            readConfig();
        }
    }

    public WidgetPosOffset getWidgetPosOffset(WidgetOffsetConfig.Key key) {
        nullCheck4ConfigKey(key);
        return widgetOffsetConfig.getWidgetPosOffset(key);
    }

    private static void nullCheck4ConfigKey(WidgetOffsetConfig.Key key) {
        if (key == null) {
            throw new NullPointerException("Fail to get widget position offset for configuration. Enum Key is null.");
        }
    }

    /**
     * Get singleton for FPConfigManager.
     * @return the singleton.
     */
    public static FPConfigManager getInstance() {
        nullCheck4Instance();
        return INSTANCE;
    }

    /**
     * Get the singleton for FPConfigManager.
     * If the config file doesn't exist, it will be created automatically.
     * @param configDirPath the configuration directory path.
     * @return the singleton
     */
    public static FPConfigManager getInstance(Path configDirPath) {
        initInstance(configDirPath);
        return INSTANCE;
    }

    private void readConfig() {
        try {
            widgetOffsetConfig = gson.fromJson(readFullConfigFile(), WidgetOffsetConfig.class);
        } catch (JsonSyntaxException e) {
            logger.error(e);
            logger.error("The json syntax for file 'filtpick.json' is illegal.");
            logger.warn("Trying to recreate a default 'filtpick.json'...");
            writeDefaultConfig();
        } catch (IllegalConfigStructure e) {
            logger.error(e);
            logger.warn("Trying to recreate a default 'filtpick.json'...");
            writeDefaultConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeDefaultConfig() {
        widgetOffsetConfig = WidgetOffsetConfig.ofDefault();
        try2Write2ConfigFile(gson.toJson(widgetOffsetConfig.configMap));
    }


    private boolean isFileEmpty() {
        return try2ReadFullConfigFile().isEmpty();
    }

    private void try2Write2ConfigFile(String content) {
        try {
            writeConfigFile(content);
        } catch (IOException e) {
            logger.error("Fail to write config to config file.");
            throw new RuntimeException(e);
        }

    }

    private void writeConfigFile(String content) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
        fileChannel.truncate(0); // Clear the original content
        fileChannel.write(buffer);
    }

    /**
     * Read config file content as String.
     * @return the content of the config file.
     */
    private String try2ReadFullConfigFile() {
        nullCheck4FileChannel();
        try {
            return readFullConfigFile();
        } catch (IOException e) {
            logger.error("Fail to readFullConfigFile config file.");
            throw new RuntimeException(e);
        }
    }

    private String readFullConfigFile() throws IOException {
        resetChannelPosition();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        StringBuilder stringBuilder = new StringBuilder();
        while (fileChannel.read(buffer) != EOF) {
            buffer.flip();
            stringBuilder.append(Charset.defaultCharset().decode(buffer));
            buffer.clear();
        }
        return stringBuilder.toString();
    }

    private void resetChannelPosition() throws IOException {
        fileChannel.position(0);
    }

    private void nullCheck4FileChannel() {
        if (fileChannel == null) {
            throw new NullPointerException("FileChannel is null.");
        }
    }

    private void initFileChannel(Path configDirPath) {
        try {
            fileChannel = FileChannel.open(configDirPath.resolve("filtpick.json"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error("Fail to resolve filtpick.json configuration file.");
            throw new RuntimeException(e);
        }
    }

    private static void initInstance(Path configDirPath) {
        INSTANCE = new FPConfigManager(configDirPath);
    }

    private static void nullCheck4Instance() {
        if (INSTANCE == null) {
            throw new NullPointerException("Config instance hasn't been initialized. Please init it via overload for FPConfigManager#getInstance first.");
        }
    }

    public static class WidgetOffsetConfig implements Adaptable<WidgetOffsetConfig> {

        public enum Key {
            RECIPE_BUTTON,
            ENTRY_BUTTON,
            FILT_MODE_BUTTON,
            DESTRUCTION_MODE_BUTTON,
            CLEAR_BUTTON,
            RETURN_BUTTON
        }
        private final Map<Key, WidgetPosOffset> configMap = new TreeMap<>();

        /**
         * Do not use this. This is specially to server Gson deserialization.
         */
        private WidgetOffsetConfig() {}

        /**
         * Return new WidgetOffsetConfig with default values initialized.
         * @return the new WidgetOffsetConfig with default values initialized
         */
        private static WidgetOffsetConfig ofDefault() {
            return new WidgetOffsetConfig().setDefaultValues();
        }

        private WidgetOffsetConfig setDefaultValues() {
            for (Key key : Key.values()) {
                configMap.put(key, WidgetPosOffset.DEFAULT);
            }
            return this;
        }

        private WidgetPosOffset getWidgetPosOffset(Key key) {
            return configMap.get(key);
        }

        private void setOffset(Key key, WidgetPosOffset offset) {
            configMap.put(key, offset);
        }

        @Override
        public TypeAdapter<WidgetOffsetConfig> getTypeAdapter() {
            return new TypeAdapter<WidgetOffsetConfig>() {
                @Override
                public void write(JsonWriter out, WidgetOffsetConfig value) throws IOException {
                    out.beginObject();
                    for (Map.Entry<Key, WidgetPosOffset> entry : configMap.entrySet()) {
                        out.name(entry.getKey().toString());
                        entry.getValue().getTypeAdapter().write(out, entry.getValue());
                    }
                    out.endObject();
                }

                @Override
                public WidgetOffsetConfig read(JsonReader in) throws IOException {
                    in.beginObject();
                    for (Key key : Key.values()) {
                        String readKey = in.nextName();
                        if (!readKey.equals(key.name())) {
                            throw new IllegalConfigStructure(String.format("Fail to read widget offset config. The key is unmatched. Need key '%s' but read '%s'.", key.name(), readKey));
                        }
                        configMap.put(key, WidgetPosOffset.DEFAULT.getTypeAdapter().read(in));
                    }
                    in.endObject();
                    return WidgetOffsetConfig.this;
                }
            }.nullSafe();
        }


    }
}
