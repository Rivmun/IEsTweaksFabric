package com.rimo.ietweaksfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {


	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}

	@Shadow
	public abstract List<ServerPlayerEntity> getPlayers();

	@Unique
	long time;

	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		time = this.properties.getTimeOfDay();
	}

	/*
	 * Consume foodLevel when player wakeUp.
	 */
	@Inject(at = @At("HEAD"), method = "wakeSleepingPlayers")
	private void wakeSleepingPlayers(CallbackInfo ci) {
		float consume = this.getServer().getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE) ?
				10 * (24000L - (time % 24000L)) / 12000F :
				6F;
		this.getPlayers().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
			player.getHungerManager().setFoodLevel((int) (player.getHungerManager().getFoodLevel() - consume));
		});
	}
}
