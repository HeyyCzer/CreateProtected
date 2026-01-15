package com.heyyczer.createprotected.mixin;

import com.heyyczer.createprotected.lib.ClaimProtected;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import dev.ftb.mods.ftbchunks.PlayerNotifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityConfigurationPacket.class)
public abstract class BlockEntityConfigurationPacketMixin {

    @Shadow
    @Final
    protected BlockPos pos;

    @Unique
    private Logger createProtected$logger = LoggerFactory.getLogger(BlockEntityConfigurationPacket.class);

    @Inject(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/networking/BlockEntityConfigurationPacket;applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/foundation/blockEntity/SyncedBlockEntity;)V"
            ),
            remap = false,
            cancellable = true
    )
    private void beforeUpdatingSettings(ServerPlayer player, CallbackInfo ci) {
        if (!ClaimProtected.canInteract(player, player.level(), this.pos)) {
            createProtected$logger.debug("{} block entity configuration was prevented due claim protection at {}",
                    player.getName().getString(), this.pos);
            PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.action_prevented").withStyle(ChatFormatting.GOLD), 2000L);
            ci.cancel();
        }
    }

}
