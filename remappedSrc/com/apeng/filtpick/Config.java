package com.apeng.filtpick;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static final Logger LOGGER = LogManager.getLogger();
    public static Config CONFIG = new Config();
    private int xOffset = 0;
    private int yOffset = 0;

    public Config(){}

    public void setxOffset(int x){
        xOffset = x;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }


    public static void  tryLoadConfigFile(MinecraftClient client)  {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve("filtpick.json");
        File configFile = configPath.toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();



        if(Files.exists(configPath)){
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(configFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                if(bufferedReader.readLine()!=null){
                    bufferedReader = new BufferedReader(new FileReader(configFile));
                    try {
                        CONFIG = gson.fromJson(bufferedReader, Config.class);
                    } catch (JsonSyntaxException | JsonIOException e) {
                        LOGGER.warn("The config file of FILTPICK does not comply with JSON syntax rules!"+"(filepath:"+ configPath +")");
                        LOGGER.warn("Recreating the config file...");
                        recreateConfigFile();
                    }

                }
                else {
                    createConfigFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                createConfigFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void createConfigFile() throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve("filtpick.json");
        File configFile = configPath.toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        configFile.createNewFile();
        String configStr = gson.toJson(CONFIG);
        FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(configStr);
        fileWriter.close();
    }

    private static void recreateConfigFile() throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve("filtpick.json");
        File configFile = configPath.toFile();
        configFile.delete();
        createConfigFile();
    }

}
