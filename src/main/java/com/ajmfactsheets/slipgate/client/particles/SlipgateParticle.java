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
	
		public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double xCoord, double yCoord, double zCoord, double xd, double yd, double zd) {
			PortalParticle portalparticle = new SlipgateParticle(level, xCoord, yCoord, zCoord, this.sprites, xd, yd, zd);
			portalparticle.pickSprite(sprites);
			return portalparticle;
		}
	}

}
