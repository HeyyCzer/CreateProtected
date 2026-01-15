package com.heyyczer.createprotected.lib;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.Protection;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClaimProtected {

    private static ClaimedChunk getClaimedChunk(Level level, BlockPos position) {
        var chunkManager = FTBChunksAPI.api().getManager();
        ChunkDimPos chunkPos = new ChunkDimPos(
                level,
                position
        );
        return chunkManager.getChunk(chunkPos);
    }

    public static boolean canInteract(Player player, Level level, BlockPos position) {
        if (level.isClientSide) return true;
        if (!(player instanceof ServerPlayer)) {
            throw new IllegalStateException("Player is not a ServerPlayer on server side!");
        }

        var chunk = getClaimedChunk(level, position);
        if (chunk == null) {
            return true;
        }

        var prevented = chunk.getTeamData().getManager().shouldPreventInteraction(
                player,
                InteractionHand.MAIN_HAND,
                position,
                Protection.INTERACT_BLOCK,
                null
        );

        return !prevented;
    }

}
