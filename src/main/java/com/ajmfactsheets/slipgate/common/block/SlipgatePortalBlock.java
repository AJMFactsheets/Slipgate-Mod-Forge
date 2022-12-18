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

	public SlipgatePortalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel level, BlockPos blockpos, RandomSource random) {
		if (level.dimensionType().natural() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
				&& random.nextInt(2000) < level.getDifficulty().getId()) {
			while (level.getBlockState(blockpos).is(this)) {
				blockpos = blockpos.below();
			}

			if (level.getBlockState(blockpos).isValidSpawn(level, blockpos, EntityType.PIGLIN)) {
				Entity entity = EntityType.PIGLIN.spawn(level, blockpos.above(), MobSpawnType.STRUCTURE);
				if (entity != null) {
					entity.setPortalCooldown();
				}
			}
		}

	}

	@Override
	public void entityInside(BlockState blockstate, Level level, BlockPos blockpos, Entity entity) {
		if (level.dimension().equals(Level.NETHER) && !entity.isPassenger() && !entity.isVehicle()
				&& entity.canChangeDimensions()) {
			entity.handleInsidePortal(blockpos);
		}

	}
	
	@Override
	public void animateTick(BlockState blockstate, Level level, BlockPos blockpos, RandomSource random) {
	      if (random.nextInt(100) == 0) {
	         level.playLocalSound((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
	      }

	      for(int i = 0; i < 4; ++i) {
	         double d0 = blockpos.getX() + random.nextDouble();
	         double d1 = blockpos.getY() + random.nextDouble();
	         double d2 = blockpos.getZ() + random.nextDouble();
	         double d3 = (random.nextFloat() - 0.5D) * 0.5D;
	         double d4 = (random.nextFloat() - 0.5D) * 0.5D;
	         double d5 = (random.nextFloat() - 0.5D) * 0.5D;
	         int j = random.nextInt(2) * 2 - 1;
	         if (!level.getBlockState(blockpos.west()).is(this) && !level.getBlockState(blockpos.east()).is(this)) {
	            d0 = blockpos.getX() + 0.5D + 0.25D * j;
	            d3 = (random.nextFloat() * 2.0F * j);
	         } else {
	            d2 = blockpos.getZ() + 0.5D + 0.25D * j;
	            d5 = (random.nextFloat() * 2.0F * j);
	         }
	         
	         level.addParticle(SlipgateMod.SLIPGATE_PORTAL_PARTICLE.get(), d0, d1, d2, d3, d4, d5);
	      }

	   }

}
