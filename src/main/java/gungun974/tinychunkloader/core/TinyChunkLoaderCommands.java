package gungun974.tinychunkloader.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import gungun974.tinychunkloader.helpers.UUIDHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkCoordinate;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TinyChunkLoaderCommands implements CommandManager.CommandRegistry {
	public static void config(ArgumentBuilder<CommandSource, ArgumentBuilderLiteral<CommandSource>> builder) {
		builder.then(ArgumentBuilderLiteral.<CommandSource>literal("config")
			.executes(context ->
				{
					Player sender = context.getSource().getSender(); if(sender == null){return 0;}

					sender.sendMessage(
						String.format("globalChunkLoadLimit : %s%d", TextFormatting.YELLOW, TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT)
					);
					sender.sendMessage(
						String.format("playerChunkLoadLimit : %s%d", TextFormatting.YELLOW, TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT)
					);

					sender.sendMessage("");

					sender.sendMessage(
						String.format("enableChunkloaderBlock : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_BLOCK)
					);
					sender.sendMessage(
						String.format("enableChunkloaderMinecart : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_MINECART)
					);
					sender.sendMessage(
						String.format("enableChunkloaderTurtle : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE)
					);

					sender.sendMessage("");

					sender.sendMessage(
						String.format("enableChunkloaderBlockCraft : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_BLOCK_CRAFT)
					);
					sender.sendMessage(
						String.format("enableChunkloaderMinecartCraft : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_MINECART_CRAFT)
					);
					sender.sendMessage(
						String.format("enableChunkloaderTurtleCraft : %s%b", TextFormatting.YELLOW, TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE_CRAFT)
					);

					sender.sendMessage("");

					sender.sendMessage(
						String.format("chunkloaderBlockRange : %s%d", TextFormatting.YELLOW, TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE)
					);
					sender.sendMessage(
						String.format("chunkloaderMinecartRange : %s%d", TextFormatting.YELLOW, TinyChunkLoader.CHUNKLOADER_MINECART_RANGE)
					);
					sender.sendMessage(
						String.format("chunkloaderTurtleRange : %s%d", TextFormatting.YELLOW, TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE)
					);

					return Command.SINGLE_SUCCESS;
				}
			)
		);
	}

	public static void status(ArgumentBuilder<CommandSource, ArgumentBuilderLiteral<CommandSource>> builder) {
		builder.then(ArgumentBuilderLiteral.<CommandSource>literal("status")
			.then(ArgumentBuilderRequired.<CommandSource, EntitySelector>argument("player", ArgumentTypeEntity.username())
				.executes(context ->
					{
						Player sender = context.getSource().getSender(); if(sender == null){return 0;}

						EntitySelector selector = context.getArgument("player", EntitySelector.class);

						final Player player = (Player)selector.get(context.getSource()).get(0);

						sender.sendMessage(
							String.format("Player : %s%s", TextFormatting.YELLOW, player.getDisplayName())
						);

						final Map<Dimension, Set<ChunkCoordinate>> chunks = ChunkLoaderManager.getInstance().getCurrentPlayerChunks(player.uuid);

						if (!chunks.isEmpty()) {
							sender.sendMessage("");

							chunks.forEach((dimension, chunkCoordinates) ->
								chunkCoordinates.forEach(coordinate ->
									sender.sendMessage(
										String.format("[%s%d%s] (%s%d,%d%s)", TextFormatting.YELLOW, dimension.id, TextFormatting.WHITE, TextFormatting.YELLOW, coordinate.x, coordinate.z, TextFormatting.WHITE)
									)
								)
							);
						}

						sender.sendMessage("");

						sender.sendMessage(
							String.format("Total : %s%d/%d", TextFormatting.YELLOW, ChunkLoaderManager.getInstance().getCurrentPlayerTotalLoads(player.uuid), TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT)
						);

						return Command.SINGLE_SUCCESS;
					}
				)
			)
			.executes(context ->
				{
					Player sender = context.getSource().getSender(); if(sender == null){return 0;}


					ChunkLoaderManager.getInstance().getCurrentPlayers().forEach((UUID uuid) -> {
						final String player = UUIDHelper.getNameFromUUID(uuid);

						sender.sendMessage(
							String.format("%s%s%s : %s%d", TextFormatting.YELLOW, player, TextFormatting.WHITE, TextFormatting.YELLOW, ChunkLoaderManager.getInstance().getCurrentPlayerTotalLoads(uuid))
						);
					});

					if (!ChunkLoaderManager.getInstance().getCurrentPlayers().isEmpty()) {
						sender.sendMessage("");
					}

					sender.sendMessage(
						String.format("Total : %s%d/%d", TextFormatting.YELLOW, ChunkLoaderManager.getInstance().getCurrentTotalLoads(), TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT)
					);

					return Command.SINGLE_SUCCESS;
				}
			)
		);
	}

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		ArgumentBuilderLiteral<CommandSource> builder = ArgumentBuilderLiteral.<CommandSource>literal("chunkloader");

		config(builder);
		status(builder);

		dispatcher.register(builder);
	}

}
