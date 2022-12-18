package com.ajmfactsheets.slipgate.common.block;

import com.ajmfactsheets.slipgate.SlipgateMod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SlipgatePortalBlock extends NetherPortalBlock {

	public SlipgatePortalBlock(Properties p_54909_) {
		super(p_54909_);
	}

	@Override
	public void randomTick(BlockState p_221799_, ServerLevel p_221800_, BlockPos p_221801_, RandomSource p_221802_) {
		if (p_221800_.dimensionType().natural() && p_221800_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
				&& p_221802_.nextInt(2000) < p_221800_.getDifficulty().getId()) {
			while (p_221800_.getBlockState(p_221801_).is(this)) {
				p_221801_ = p_221801_.below();
			}

			if (p_221800_.getBlockState(p_221801_).isValidSpawn(p_221800_, p_221801_, EntityType.PIGLIN)) {
				Entity entity = EntityType.PIGLIN.spawn(p_221800_, p_221801_.above(), MobSpawnType.STRUCTURE);
				if (entity != null) {
					entity.setPortalCooldown();
				}
			}
		}

	}

	@Override
	public void entityInside(BlockState p_54915_, Level p_54916_, BlockPos p_54917_, Entity p_54918_) {
		if (p_54916_.dimension().equals(Level.NETHER) && !p_54918_.isPassenger() && !p_54918_.isVehicle()
				&& p_54918_.canChangeDimensions()) {
			p_54918_.handleInsidePortal(p_54917_);
		}

	}
	
	@Override
	public void animateTick(BlockState p_221794_, Level p_221795_, BlockPos p_221796_, RandomSource p_221797_) {
	      if (p_221797_.nextInt(100) == 0) {
	         p_221795_.playLocalSound((double)p_221796_.getX() + 0.5D, (double)p_221796_.getY() + 0.5D, (double)p_221796_.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, p_221797_.nextFloat() * 0.4F + 0.8F, false);
	      }

	      for(int i = 0; i < 4; ++i) {
	         double d0 = (double)p_221796_.getX() + p_221797_.nextDouble();
	         double d1 = (double)p_221796_.getY() + p_221797_.nextDouble();
	         double d2 = (double)p_221796_.getZ() + p_221797_.nextDouble();
	         double d3 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
	         double d4 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
	         double d5 = ((double)p_221797_.nextFloat() - 0.5D) * 0.5D;
	         int j = p_221797_.nextInt(2) * 2 - 1;
	         if (!p_221795_.getBlockState(p_221796_.west()).is(this) && !p_221795_.getBlockState(p_221796_.east()).is(this)) {
	            d0 = (double)p_221796_.getX() + 0.5D + 0.25D * (double)j;
	            d3 = (double)(p_221797_.nextFloat() * 2.0F * (float)j);
	         } else {
	            d2 = (double)p_221796_.getZ() + 0.5D + 0.25D * (double)j;
	            d5 = (double)(p_221797_.nextFloat() * 2.0F * (float)j);
	         }
	         
	         p_221795_.addParticle(SlipgateMod.SLIPGATE_PORTAL_PARTICLE.get(), d0, d1, d2, d3, d4, d5);
	      }

	   }

}
