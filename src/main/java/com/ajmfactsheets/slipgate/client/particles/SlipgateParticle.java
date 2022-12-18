package com.ajmfactsheets.slipgate.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlipgateParticle extends PortalParticle {

	protected SlipgateParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
            SpriteSet spriteSet, double xd, double yd, double zd) {
		super(level, xCoord, yCoord, zCoord, xd, yd, zd);
		
		this.setSpriteFromAge(spriteSet);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;
		
		public Provider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}
	
		public Particle createParticle(SimpleParticleType p_107581_, ClientLevel p_107582_, double p_107583_, double p_107584_, double p_107585_, double p_107586_, double p_107587_, double p_107588_) {
			PortalParticle portalparticle = new SlipgateParticle(p_107582_, p_107583_, p_107584_, p_107585_, this.sprites, p_107586_, p_107587_, p_107588_);
			portalparticle.pickSprite(sprites);
			return portalparticle;
		}
	}

}
