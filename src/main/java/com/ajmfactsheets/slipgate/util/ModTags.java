package com.ajmfactsheets.slipgate.util;

import com.ajmfactsheets.slipgate.SlipgateMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
	public static class Blocks {
		public static final TagKey<Block> SLIPGATE_FRAME_BLOCKS = tag("slipgate_frame_blocks");
		
		private static TagKey<Block> tag(String name) {
			return BlockTags.create(new ResourceLocation(SlipgateMod.MODID, name));
		}
		
	}
	
	public static class Items {
		
	}
}
