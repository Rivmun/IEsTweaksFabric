package com.rimo.ietweaksfabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {

	protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow public void finalizeSpawnChildFromBreeding(ServerLevel leve, Animal animal, @Nullable AgeableMob ageableMob) {}

	@Inject(at = @At("HEAD"), method = "spawnChildFromBreeding")
	public void iet$pigLayMoreBaby(ServerLevel level, Animal other, CallbackInfo info) {
		if (other instanceof Pig) {
			int i = (int) (Math.random() * 3);
			while (i-- > 0) {
				AgeableMob ageableMob = ((Pig) (Object) this).getBreedOffspring(level, other);
				if (ageableMob != null) {
					ageableMob.setBaby(true);
					ageableMob.snapTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
					finalizeSpawnChildFromBreeding(level, other, ageableMob);
					level.addFreshEntityWithPassengers(ageableMob);
				}
			}
		}
	}
}
