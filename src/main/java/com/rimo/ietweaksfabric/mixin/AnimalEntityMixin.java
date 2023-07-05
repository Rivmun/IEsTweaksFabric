package com.rimo.ietweaksfabric.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

	protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract @Nullable ServerPlayerEntity getLovingPlayer();

	/*
	 * Pig lays 1-3 child(s).
	 */
	@Inject(at = @At("HEAD"), method = "breed")
	public void breed(ServerWorld world, AnimalEntity other, CallbackInfo info) {
		if (other instanceof PigEntity) {
			int i = (int) (Math.random() * 3);
			while (i-- > 0) {
				PassiveEntity child = this.createChild(world, other);
				if (child != null) {
					if (this.getLovingPlayer() != null)
						Criteria.BRED_ANIMALS.trigger(this.getLovingPlayer(), (AnimalEntity) (Object) this, other, child);
					child.setBaby(true);
					child.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
					world.spawnEntityAndPassengers(child);
				}
			}
		}
	}
}
