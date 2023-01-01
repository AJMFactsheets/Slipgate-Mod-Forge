package com.ajmfactsheets.slipgate.common.item;

import com.ajmfactsheets.slipgate.SlipgateMod;
import com.ajmfactsheets.slipgate.common.block.SlipgatePortalBlock;
import com.ajmfactsheets.slipgate.world.dimension.SlipDimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FlintAndGoldItem extends Item {

	public FlintAndGoldItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer() != null) {
			if (context.getPlayer().level.dimension() == SlipDimension.SLIP_KEY
					|| context.getPlayer().level.dimension() == Level.NETHER) {
				for (Direction direction : Direction.Plane.VERTICAL) {
					BlockPos framePos = context.getClickedPos().relative(direction);
					if (((SlipgatePortalBlock) SlipgateMod.SLIPGATE_PORTAL_BLOCK.get())
							.trySpawnPortal(context.getLevel(), framePos)) {
						context.getLevel().playSound(context.getPlayer(), framePos, SoundEvents.FLINTANDSTEEL_USE,
								SoundSource.BLOCKS, 1.0F, 1.0F);
						Player player = context.getPlayer();
						Level level = context.getLevel();
						ItemStack itemstack = player.getItemInHand(context.getHand());
			            if (player instanceof ServerPlayer) {
			               itemstack.hurtAndBreak(1, player, (breakEvent) -> {
			                  breakEvent.broadcastBreakEvent(context.getHand());
			               });
			            }
			            return InteractionResult.sidedSuccess(level.isClientSide());
					} else
						return InteractionResult.FAIL;
				}
			}
		}
		return InteractionResult.FAIL;
	}
}
