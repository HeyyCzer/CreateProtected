package com.heyyczer.createprotected.mixin;

import com.heyyczer.createprotected.lib.ClaimProtected;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.ftb.mods.ftbchunks.PlayerNotifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilteringBehaviour.class)
public abstract class FilteringBehaviourMixin {

    @Unique
    private Logger createProtected$logger = LoggerFactory.getLogger(FilteringBehaviour.class);

    @Inject(method = "onShortInteract", at = @At("HEAD"), cancellable = true)
    private void beforeOnShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult, CallbackInfo ci) {
        var blockPos = hitResult.getBlockPos();
        if (!ClaimProtected.canInteract(player, player.level(), blockPos)) {
            createProtected$logger.debug("Prevented changing filter item at {} by player {}", blockPos, player.getName().getString());
            PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.action_prevented").withStyle(ChatFormatting.GOLD), 2000L);
            ci.cancel();
        }
    }

    @Inject(
            method = "setValueSettings",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;count:I",
                    opcode = Opcodes.PUTFIELD
            ),
            remap = false,
            cancellable = true
    )
    private void beforeUpdatingValueSettings(Player player, ValueSettingsBehaviour.ValueSettings settings, boolean ctrlDown, CallbackInfo ci) {
        FilteringBehaviour self = (FilteringBehaviour) (Object) this;
        var blockEntity = self.blockEntity;
        var blockPos = blockEntity.getBlockPos();

        if (!ClaimProtected.canInteract(player, player.level(), blockPos)) {
            createProtected$logger.debug("Prevented changing filter value settings at {} by player {}", blockPos, player.getName().getString());
            PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.action_prevented").withStyle(ChatFormatting.GOLD), 2000L);
            ci.cancel();
        }
    }

}
