package com.phasetranscrystal.nonard.testobjs;

import com.phasetranscrystal.nonard.Nonard;
import io.github.tt432.eyelib.client.loader.BrParticleLoader;
import io.github.tt432.eyelib.client.particle.bedrock.BrParticleEmitter;
import io.github.tt432.eyelib.client.particle.bedrock.BrParticleManager;
import io.github.tt432.eyelib.client.particle.bedrock.BrParticleParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ParticlesTest {
    public static final ResourceLocation LARGE_SPORE_RING_SPRAY = ResourceLocation.fromNamespaceAndPath(Nonard.MOD_ID,"large_spore_ring_spray");


    public static class Emitter extends Item{
        public Emitter() {
            super(new Properties());
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if(level.isClientSide()) {
                new BrParticleEmitter(BrParticleLoader.getParticle(LARGE_SPORE_RING_SPRAY),null,level, player.getPosition(0).toVector3f()).emit();
            }
            return super.use(level, player, usedHand);
        }
    }
}
