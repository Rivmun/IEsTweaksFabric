package com.rimo.ietweaksfabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(at = @At("RETURN"), method = "getAttributeValue", cancellable = true)
	public void getAttributeValue(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
		/*
		 * Horse 20% faster,
		 */
		if (((LivingEntity) (Object) this) instanceof AbstractHorseEntity && attribute.getTranslationKey().equals(EntityAttributes.GENERIC_MOVEMENT_SPEED.getTranslationKey()))
			cir.setReturnValue(cir.getReturnValue() * 1.2f);
		/*
		 * Zombie 20% unknockbackable.
		 */
		if (((LivingEntity) (Object) this) instanceof ZombieEntity && attribute.getTranslationKey().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getTranslationKey()))
			cir.setReturnValue(cir.getReturnValue() + 0.2f);
	}

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);

    /*
     * Player get blindness when damaged over 6 points health.
     */
	@Inject(at = @At("HEAD"), method = "setHealth")
	public void setHealth(float health, CallbackInfo ci) {
		if (((LivingEntity) (Object) this) instanceof PlayerEntity player && player.getHealth() - health > 6f)
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));
	}
}
