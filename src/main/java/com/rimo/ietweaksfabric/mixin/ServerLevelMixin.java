package com.rimo.ietweaksfabric.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Shadow public abstract List<ServerPlayer> players();
	@Shadow public abstract MinecraftServer getServer();
	@Shadow @Final private ServerLevelData serverLevelData;
	@Unique long iet$time;

	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		iet$time = this.serverLevelData.getDayTime();
	}

	@Inject(at = @At("HEAD"), method = "wakeUpAllPlayers")
	private void iet$consumeFoodWhenWakeUp(CallbackInfo ci) {
		float consume = this.getServer().getWorldData().getGameRules().get(GameRules.ADVANCE_TIME) ?
				10 * (24000L - (iet$time % 24000L)) / 12000F :
				6F;
		this.players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
			player.getFoodData().setFoodLevel((int) (player.getFoodData().getFoodLevel() - consume));
		});
	}
}
