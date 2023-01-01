package com.ajmfactsheets.slipgate.world.dimension;

import org.slf4j.Logger;

import com.ajmfactsheets.slipgate.SlipgateMod;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Controlled by data packs in the resources folder.
 * 
 * @author AJMFactsheets
 *
 */
public class SlipDimension {
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static final ResourceKey<Level> SLIP_KEY = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(SlipgateMod.MODID, "the_slip"));
	public static final ResourceKey<DimensionType> SLIP_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, SLIP_KEY.location());
	
	public static void register() {
		LOGGER.info("Registering " + SlipgateMod.MODID + " dimension!");
	}
}
