package com.heyyczer.createprotected.mixin;

import com.heyyczer.createprotected.lib.ClaimProtected;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.ftb.mods.ftbchunks.PlayerNotifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScrollValueBehaviour.class)
public abstract class ScrollValueBehaviourMixin {

    @Unique
    private Logger createProtected$logger = LoggerFactory.getLogger(ScrollValueBehaviour.class);

    @Inject(method = "setValueSettings", at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/scrollValue/ScrollValueBehaviour;setValue(I)V"
    ), cancellable = true)
    public void beforeUpdatingValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlDown, CallbackInfo ci) {
        ScrollValueBehaviour self = (ScrollValueBehaviour) (Object) this;
        var blockEntity = self.blockEntity;

        if (!ClaimProtected.canInteract(player, player.level(), blockEntity.getBlockPos())) {
            createProtected$logger.debug("Prevented changing scroll value settings at {} by player {}", blockEntity.getBlockPos(), player.getName().getString());
            PlayerNotifier.notifyWithCooldown(player, Component.translatable("ftbchunks.action_prevented").withStyle(ChatFormatting.GOLD), 2000L);
            ci.cancel();
        }
    }

}
