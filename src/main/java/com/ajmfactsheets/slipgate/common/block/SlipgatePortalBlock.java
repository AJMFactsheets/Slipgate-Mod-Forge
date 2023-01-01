package com.ajmfactsheets.slipgate.common.block;

import javax.annotation.Nullable;

import com.ajmfactsheets.slipgate.SlipgateMod;
import com.ajmfactsheets.slipgate.util.ModTags;
import com.ajmfactsheets.slipgate.world.dimension.SlipDimension;
import com.ajmfactsheets.slipgate.world.dimension.portal.ModTeleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class SlipgatePortalBlock extends NetherPortalBlock {

	public SlipgatePortalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel level, BlockPos blockpos, RandomSource random) {
		if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < level.getDifficulty().getId()) {
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
		if ((Level.NETHER.equals(level.dimension()) || SlipDimension.SLIP_KEY.equals(level.dimension())) && !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
			if (entity.isOnPortalCooldown()) {
				entity.setPortalCooldown();
			} else {
				if (!entity.level.isClientSide && !blockpos.equals(entity.portalEntrancePos)) {
					entity.portalEntrancePos = blockpos.immutable();
				}
				Level entityWorld = entity.level;
				if (entityWorld != null) {
					MinecraftServer minecraftserver = entityWorld.getServer();
					ResourceKey<Level> destination = SlipDimension.SLIP_KEY.equals(entity.level.dimension()) ? Level.NETHER : SlipDimension.SLIP_KEY;
					if (minecraftserver != null) {
						ServerLevel destinationWorld = minecraftserver.getLevel(destination);
						if (destinationWorld != null && minecraftserver.isNetherEnabled() && !entity.isPassenger()) {
							entity.level.getProfiler().push("portal");
							entity.setPortalCooldown();
							entity.changeDimension(destinationWorld, new ModTeleporter(destinationWorld));
							entity.level.getProfiler().pop();
						}
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState blockstate, Level level, BlockPos blockpos, RandomSource random) {
		if (random.nextInt(100) == 0) {
			level.playLocalSound((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D,
					(double) blockpos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F,
					random.nextFloat() * 0.4F + 0.8F, false);
		}

		for (int i = 0; i < 4; ++i) {
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

	public boolean trySpawnPortal(LevelAccessor worldIn, BlockPos pos) {
		SlipgatePortalBlock.Size KJPortalBlock$size = this.isPortal(worldIn, pos);
		if (KJPortalBlock$size != null && !onTrySpawnPortal(worldIn, pos, KJPortalBlock$size)) {
			KJPortalBlock$size.placePortalBlocks();
			return true;
		} else {
			return false;
		}
	}

	public static boolean onTrySpawnPortal(LevelAccessor world, BlockPos pos, SlipgatePortalBlock.Size size) {
		return MinecraftForge.EVENT_BUS.post(new PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
	}

	@Cancelable
	public static class PortalSpawnEvent extends BlockEvent {
		private final SlipgatePortalBlock.Size size;

		public PortalSpawnEvent(LevelAccessor world, BlockPos pos, BlockState state, SlipgatePortalBlock.Size size) {
			super(world, pos, state);
			this.size = size;
		}

		public SlipgatePortalBlock.Size getPortalSize() {
			return size;
		}
	}

	@Nullable
	public SlipgatePortalBlock.Size isPortal(LevelAccessor worldIn, BlockPos pos) {
		SlipgatePortalBlock.Size KJPortalBlock$size = new Size(worldIn, pos, Direction.Axis.X);
		if (KJPortalBlock$size.isValid() && KJPortalBlock$size.portalBlockCount == 0) {
			return KJPortalBlock$size;
		} else {
			SlipgatePortalBlock.Size KaupenPortalBlock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
			return KaupenPortalBlock$size1.isValid() && KaupenPortalBlock$size1.portalBlockCount == 0
					? KaupenPortalBlock$size1
					: null;
		}
	}

	public static class Size {
		private final LevelAccessor level;
		private final Direction.Axis axis;
		private final Direction rightDir;
		private final Direction leftDir;
		private int portalBlockCount;
		@Nullable
		private BlockPos bottomLeft;
		private int height;
		private int width;

		public Size(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
			this.level = level;
			this.axis = axis;
			if (axis == Direction.Axis.X) {
				this.leftDir = Direction.EAST;
				this.rightDir = Direction.WEST;
			} else {
				this.leftDir = Direction.NORTH;
				this.rightDir = Direction.SOUTH;
			}

			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0
					&& this.canConnect(level.getBlockState(pos.below())); pos = pos.below()) {
			}

			int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
			if (i >= 0) {
				this.bottomLeft = pos.relative(this.leftDir, i);
				this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
				if (this.width < 2 || this.width > 21) {
					this.bottomLeft = null;
					this.width = 0;
				}
			}

			if (this.bottomLeft != null) {
				this.height = this.calculatePortalHeight();
			}

		}

		protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
			int i;
			for (i = 0; i < 22; ++i) {
				BlockPos blockpos = pos.relative(directionIn, i);
				if (!this.canConnect(this.level.getBlockState(blockpos))
						|| !(this.level.getBlockState(blockpos.below()).is(ModTags.Blocks.SLIPGATE_FRAME_BLOCKS))) {
					break;
				}
			}

			BlockPos framePos = pos.relative(directionIn, i);
			return this.level.getBlockState(framePos).is(ModTags.Blocks.SLIPGATE_FRAME_BLOCKS) ? i : 0;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		protected int calculatePortalHeight() {
			label56: for (this.height = 0; this.height < 21; ++this.height) {
				for (int i = 0; i < this.width; ++i) {
					BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
					BlockState blockstate = this.level.getBlockState(blockpos);
					if (!this.canConnect(blockstate)) {
						break label56;
					}

					Block block = blockstate.getBlock();
					if (block == SlipgateMod.SLIPGATE_PORTAL_BLOCK.get()) {
						++this.portalBlockCount;
					}

					if (i == 0) {
						BlockPos framePos = blockpos.relative(this.leftDir);
						if (!(this.level.getBlockState(framePos).is(ModTags.Blocks.SLIPGATE_FRAME_BLOCKS))) {
							break label56;
						}
					} else if (i == this.width - 1) {
						BlockPos framePos = blockpos.relative(this.rightDir);
						if (!(this.level.getBlockState(framePos).is(ModTags.Blocks.SLIPGATE_FRAME_BLOCKS))) {
							break label56;
						}
					}
				}
			}

			for (int j = 0; j < this.width; ++j) {
				BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
				if (!(this.level.getBlockState(framePos).is(ModTags.Blocks.SLIPGATE_FRAME_BLOCKS))) {
					this.height = 0;
					break;
				}
			}

			if (this.height <= 21 && this.height >= 3) {
				return this.height;
			} else {
				this.bottomLeft = null;
				this.width = 0;
				this.height = 0;
				return 0;
			}
		}

		protected boolean canConnect(BlockState pos) {
			Block block = pos.getBlock();
			return pos.isAir() || block == SlipgateMod.SLIPGATE_PORTAL_BLOCK.get();
		}

		public boolean isValid() {
			return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3
					&& this.height <= 21;
		}

		public void placePortalBlocks() {
			for (int i = 0; i < this.width; ++i) {
				BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

				for (int j = 0; j < this.height; ++j) {
					this.level.setBlock(blockpos.above(j), SlipgateMod.SLIPGATE_PORTAL_BLOCK.get().defaultBlockState()
							.setValue(SlipgatePortalBlock.AXIS, this.axis), 18);
				}
			}

		}

		private boolean isPortalCountValidForSize() {
			return this.portalBlockCount >= this.width * this.height;
		}

		public boolean validatePortal() {
			return this.isValid() && this.isPortalCountValidForSize();
		}
	}

}
