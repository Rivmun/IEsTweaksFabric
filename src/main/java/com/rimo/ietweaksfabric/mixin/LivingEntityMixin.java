package com.rimo.ietweaksfabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(at = @At("RETURN"), method = "getAttributeValue", cancellable = true)
	public void iet$getAttributeValue(Holder<Attribute> holder, CallbackInfoReturnable<Double> cir) {
		/*
		 * Horse 20% faster.
		 */
		if (((LivingEntity) (Object) this) instanceof AbstractHorse && holder.unwrapKey().equals(Attributes.MOVEMENT_SPEED.unwrapKey()))
			cir.setReturnValue(cir.getReturnValue() * 1.2f);
		/*
		 * Zombie 20% unknockbackable.
		 */
		if (((LivingEntity) (Object) this) instanceof Zombie && holder.unwrapKey().equals(Attributes.KNOCKBACK_RESISTANCE.unwrapKey()))
			cir.setReturnValue(cir.getReturnValue() + 0.2f);
	}

	@Shadow
	public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

	/*
	 * Player get blindness when damaged 1/3 health.
	 */
	@Inject(at = @At("HEAD"), method = "setHealth")
	public void iet$setHealth(float health, CallbackInfo ci) {
		if (((LivingEntity) (Object) this) instanceof ServerPlayer player && player.connection != null && (player.getHealth() - health) / player.getMaxHealth() > 0.33f)
			this.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
	}

	/*
	 * Player get slowness when get fall damage.
	 */
	@Inject(at = @At("RETURN"), method = "calculateFallDamage")
	public void iet$calculateFallDamage(double fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
		int i = cir.getReturnValue();
		if (i > 0)
			this.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, Math.max(60, i * 10), Math.min(4, i / 4)));
	}
}
