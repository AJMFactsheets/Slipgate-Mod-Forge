package com.ajmfactsheets.slipgate;

import org.slf4j.Logger;

import com.ajmfactsheets.slipgate.client.particles.SlipgateParticle;
import com.ajmfactsheets.slipgate.common.block.SlipgatePortalBlock;
import com.ajmfactsheets.slipgate.common.item.FlintAndGoldItem;
import com.ajmfactsheets.slipgate.world.dimension.SlipDimension;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(SlipgateMod.MODID)
public class SlipgateMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "slipgate";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, SlipgateMod.MODID);

    public static final RegistryObject<Block> SLIPGATE_PORTAL_BLOCK = BLOCKS.register("slipgate_portal", () -> new SlipgatePortalBlock(BlockBehaviour.Properties.of(Material.PORTAL).noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel((level) -> {return 11;})));
    public static final RegistryObject<Item> FLINT_AND_GOLD = ITEMS.register("flint_and_gold", () -> new FlintAndGoldItem(new Item.Properties().durability(8)));
    public static final RegistryObject<SimpleParticleType> SLIPGATE_PORTAL_PARTICLE = PARTICLE_TYPES.register("slipgate_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<PoiType> SLIPGATE_POI = POI.register("slipgate", () -> new PoiType(ImmutableSet.copyOf(SLIPGATE_PORTAL_BLOCK.get().getStateDefinition().getPossibleStates()), 0, 1));
    
    public SlipgateMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        PARTICLE_TYPES.register(modEventBus);
        POI.register(modEventBus);
        
        SlipDimension.register();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(FLINT_AND_GOLD);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents
    {
        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        	event.register(SLIPGATE_PORTAL_PARTICLE.get(), SlipgateParticle.Provider::new);
        }
    }
}
