package gungun974.tinychunkloader.core;
;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;

import static gungun974.tinychunkloader.core.TinyChunkLoader.MOD_ID;

public class TinyChunkLoaderConfig {
	public static TomlConfigHandler config;

	public static void RegisterConfig() {
		Toml defaultConfig = new Toml("TinyChunkLoader configuration file.");
		defaultConfig.addEntry("globalChunkLoadLimit", "Maximum number of chunk loader active in every dimensions",1024);

		defaultConfig.addEntry("playerChunkLoadLimit", "Maximum number of chunk loader active per players in every dimensions",1024);

		defaultConfig.addEntry("enableChunkloaderBlock", "Enable chunk loading with the chunk loader block",true);
		defaultConfig.addEntry("enableChunkloaderMinecart", "Enable chunk loading with the chunk loader minecart",true);
		defaultConfig.addEntry("enableChunkloaderTurtle", "Enable chunk loading with the chunk loader turtle",true);

		defaultConfig.addEntry("enableChunkloaderBlockCraft", "Enable craft for the chunk loader block",true);
		defaultConfig.addEntry("enableChunkloaderMinecartCraft", "Enable craft for the chunk loader minecart",true);
		defaultConfig.addEntry("enableChunkloaderTurtleCraft", "Enable craft for the chunk loader turtle",true);

		defaultConfig.addEntry("chunkloaderBlockRange", "Range of the chunk loader block in a square radius (Example: 2 will be a 3x3 loads)",2);
		defaultConfig.addEntry("chunkloaderMinecartRange", "Range of the chunk loader minecart in a square radius",2);
		defaultConfig.addEntry("chunkloaderTurtleRange", "Range of the chunk loader turtle in a square radius",2);

		config = new TomlConfigHandler(MOD_ID, new Toml("TinyChunkLoader configuration file."),false);

		File configFile = config.getConfigFile();

		if (config.getConfigFile().exists()) {
			config.loadConfig();
			config.setDefaults(config.getRawParsed());
			Toml rawConfig = config.getRawParsed();
			boolean changed = false;

			if(!rawConfig.contains("globalChunkLoadLimit")){
				rawConfig.addEntry("globalChunkLoadLimit", "Maximum number of chunk loader active in every dimensions", 1024);
				changed = true;
			}

			if(!rawConfig.contains("playerChunkLoadLimit")){
				rawConfig.addEntry("playerChunkLoadLimit", "Maximum number of chunk loader active per players in every dimensions", 1024);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderBlock")){
				rawConfig.addEntry("enableChunkloaderBlock", "Enable chunk loading with the chunk loader block", true);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderMinecart")){
				rawConfig.addEntry("enableChunkloaderMinecart", "Enable chunk loading with the chunk loader minecart", true);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderTurtle")){
				rawConfig.addEntry("enableChunkloaderTurtle", "Enable chunk loading with the chunk loader turtle", true);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderBlockCraft")){
				rawConfig.addEntry("enableChunkloaderBlockCraft", "Enable craft for the chunk loader block", true);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderMinecartCraft")){
				rawConfig.addEntry("enableChunkloaderMinecartCraft", "Enable craft for the chunk loader minecart", true);
				changed = true;
			}

			if(!rawConfig.contains("enableChunkloaderTurtleCraft")){
				rawConfig.addEntry("enableChunkloaderTurtleCraft", "Enable craft for the chunk loader turtle", true);
				changed = true;
			}

			if(!rawConfig.contains("chunkloaderBlockRange")){
				rawConfig.addEntry("chunkloaderBlockRange", "Range of the chunk loader block in a square radius (Example: 2 will be a 3x3 loads)", 2);
				changed = true;
			}

			if(!rawConfig.contains("chunkloaderMinecartRange")){
				rawConfig.addEntry("chunkloaderMinecartRange", "Range of the chunk loader minecart in a square radius", 2);
				changed = true;
			}

			if(!rawConfig.contains("chunkloaderTurtleRange")){
				rawConfig.addEntry("chunkloaderTurtleRange", "Range of the chunk loader turtle in a square radius", 2);
				changed = true;
			}

			if (changed) {
				config.setDefaults(rawConfig);
				config.writeConfig();
				config.loadConfig();
			}
		} else {
			config.setDefaults(defaultConfig);
			try {
				//noinspection ResultOfMethodCallIgnored
				configFile.getParentFile().mkdirs();
				//noinspection ResultOfMethodCallIgnored
				configFile.createNewFile();
				config.writeConfig();
				config.loadConfig();
			} catch (IOException e) {
				throw new RuntimeException("Failed to generate config!", e);
			}
		}

		TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT = config.getInt("globalChunkLoadLimit");
		TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT = config.getInt("playerChunkLoadLimit");

		TinyChunkLoader.ENABLE_CHUNKLOADER_BLOCK = config.getBoolean("enableChunkloaderBlock");
		TinyChunkLoader.ENABLE_CHUNKLOADER_MINECART = config.getBoolean("enableChunkloaderMinecart");
		TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE = config.getBoolean("enableChunkloaderTurtle");

		TinyChunkLoader.ENABLE_CHUNKLOADER_BLOCK_CRAFT = config.getBoolean("enableChunkloaderBlockCraft");
		TinyChunkLoader.ENABLE_CHUNKLOADER_MINECART_CRAFT = config.getBoolean("enableChunkloaderMinecartCraft");
		TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE_CRAFT = config.getBoolean("enableChunkloaderTurtleCraft");

		TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE = config.getInt("chunkloaderBlockRange");
		TinyChunkLoader.CHUNKLOADER_MINECART_RANGE = config.getInt("chunkloaderMinecartRange");
		TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE = config.getInt("chunkloaderTurtleRange");
	}
}
