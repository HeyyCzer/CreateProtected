package com.heyyczer.createprotected.mixin;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbchunks.FTBChunks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FTBChunks.class)
public abstract class BlockRightClickMixin {

    @Unique
    private static final List<String> ALLOWED_BLOCKS = List.of(
            "numismatics:bank_terminal"
    );

    // Allow click in Numismatics Bank Terminal
    @Inject(
            method = "blockRightClick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRightClick(Player player, InteractionHand hand, BlockPos pos, Direction face, CallbackInfoReturnable<EventResult> cir) {
        if (player instanceof ServerPlayer sp) {
            var blockHolder = sp.level().getBlockState(pos).getBlockHolder();
            if (blockHolder != null && ALLOWED_BLOCKS.contains(blockHolder.getRegisteredName())) {
                cir.setReturnValue(EventResult.pass());
            }
        }
    }

}
