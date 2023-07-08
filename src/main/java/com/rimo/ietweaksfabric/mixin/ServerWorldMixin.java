package com.rimo.ietweaksfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow
    public abstract List<ServerPlayerEntity> getPlayers();

    /*
     * Consume 6 point of foodLevel when player wakeUp.
     */
    @Inject(at = @At("HEAD"), method = "wakeSleepingPlayers")
    private void wakeSleepingPlayers(CallbackInfo ci) {
        this.getPlayers().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
            player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel() - 6);
        });
    }
}
