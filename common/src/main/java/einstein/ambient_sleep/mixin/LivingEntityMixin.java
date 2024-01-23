package einstein.ambient_sleep.mixin;

import einstein.ambient_sleep.init.ModParticles;
import einstein.ambient_sleep.init.ModSounds;
import einstein.ambient_sleep.util.ParticleEmittingEntity;
import einstein.ambient_sleep.util.Util;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract boolean isSleeping();

    @Shadow
    public abstract float getVoicePitch();

    @Shadow
    public int hurtTime;
    @Unique
    private int ambientSleep$breatheTimer = 0;

    @Unique
    private int ambientSleep$snoreTimer = 0;

    @Unique
    private int ambientSleep$snoreCount = 0;

    @SuppressWarnings("all")
    @Unique
    private final LivingEntity ambientSleep$me = (LivingEntity) (Object) this;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (level().isClientSide) {
            if (isSleeping()) {
                if (ambientSleep$breatheTimer < Util.BREATH_DELAY) {
                    ambientSleep$breatheTimer++;
                }
                else {
                    if (ambientSleep$snoreTimer >= Util.SNORE_DELAY) {
                        if (ambientSleep$snoreCount <= 0) {
                            if (ambientSleep$me instanceof Player) {
                                Util.playClientSound(SoundSource.PLAYERS, ambientSleep$me, ModSounds.PLAYER_SLEEP.get(), 1, getVoicePitch());
                            }
                            else if (ambientSleep$me instanceof Villager) {
                                Util.playClientSound(SoundSource.NEUTRAL, ambientSleep$me, ModSounds.VILLAGER_SLEEP.get(), 1, getVoicePitch());
                            }
                        }

                        ambientSleep$snoreTimer = 0;
                        ambientSleep$snoreCount++;
                        level().addParticle(ModParticles.SNORING.get(), getX(), getY() + 0.5, getZ(), 0, 0, 0);

                        if (ambientSleep$snoreCount >= 3) {
                            ambientSleep$snoreCount = 0;
                            ambientSleep$breatheTimer = 0;
                        }
                    }

                    if (ambientSleep$snoreTimer < Util.SNORE_DELAY) {
                        ambientSleep$snoreTimer++;
                    }
                }
            }
            else {
                ambientSleep$breatheTimer = 0;
                ambientSleep$snoreTimer = 0;
                ambientSleep$snoreCount = 0;
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ambientSleep$me instanceof ParticleEmittingEntity entity) {
            if (level().isClientSide && !isInvulnerableTo(source) && amount > 0) {
                if (source.getEntity() instanceof LivingEntity && isAlive() && hurtTime == 0) {
                    entity.ambientSleep$spawnParticles(level(), ambientSleep$me, random);
                }
            }
        }
    }
}