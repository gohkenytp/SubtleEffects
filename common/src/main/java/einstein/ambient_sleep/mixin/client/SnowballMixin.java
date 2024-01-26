package einstein.ambient_sleep.mixin.client;

import einstein.ambient_sleep.init.ModParticles;
import einstein.ambient_sleep.util.ParticleEmittingEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Snowball.class)
public class SnowballMixin implements ParticleEmittingEntity {

    @Override
    public void ambientSleep$spawnParticles(Level level, Entity entity, RandomSource random) {
        level.addParticle(ModParticles.SNOWBALL_TRAIL.get(), entity.getRandomX(1), entity.getRandomY(), entity.getRandomZ(1), 0, 0, 0);
    }
}