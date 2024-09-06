package com.rimo.ietweaksfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(at = @At("RETURN"), method = "getAttributeValue", cancellable = true)
	public void getAttributeValue(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
		/*
		 * Horse 20% faster.
		 * (IDEA mark these lines as warning, just ignored... or add a parameter to catch the 'instanceof' result. But it's no sense.)
		 */
		if (((LivingEntity) (Object) this) instanceof AbstractHorseEntity a && attribute.getKey().equals(EntityAttributes.GENERIC_MOVEMENT_SPEED.getKey()))
			cir.setReturnValue(cir.getReturnValue() * 1.2f);
		/*
		 * Zombie 20% unknockbackable.
		 */
		if (((LivingEntity) (Object) this) instanceof ZombieEntity a && attribute.getKey().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getKey()))
			cir.setReturnValue(cir.getReturnValue() + 0.2f);
	}

	@Shadow
	public abstract boolean addStatusEffect(StatusEffectInstance effect);

	/*
	 * Player get blindness when damaged over 6 points health.
	 */
	@Inject(at = @At("HEAD"), method = "damage")
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (((LivingEntity) (Object) this) instanceof ServerPlayerEntity player && player.networkHandler != null && amount / player.getMaxHealth() > 0.33f)
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
	}

	/*
	 * Player get slowness when get fall damage.
	 */
	@Inject(at = @At("RETURN"), method = "computeFallDamage")
	public void computeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
		int i = cir.getReturnValue();
		if (i != 0)
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Math.max(60, i * 10), Math.min(4, i / 4)));
	}
}
