package einstein.subtle_effects;

import einstein.subtle_effects.init.ModParticles;
import einstein.subtle_effects.platform.ForgeNetworkHelper;
import einstein.subtle_effects.platform.ForgeRegistryHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static einstein.subtle_effects.SubtleEffects.MOD_ID;

@Mod(MOD_ID)
public class SubtleEffectsForge {

    public SubtleEffectsForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SubtleEffects.init();
        ModParticles.init();
        ForgeRegistryHelper.PARTICLE_TYPES.register(modEventBus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new SubtleEffectsForgeClient(modEventBus));
        modEventBus.addListener((FMLCommonSetupEvent event) -> ForgeNetworkHelper.init());
        ForgeRegistryHelper.SOUND_EVENTS.register(modEventBus);
    }
}