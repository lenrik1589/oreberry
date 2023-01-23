package here.alice.oreberry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Initializer implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("OreBerry");

	public static final OreBerry IRON_BERRY     = registerBerryBlock(OreBerry.create("iron", OreBerry.Placement.FLOOR, 2));
	public static final OreBerry GOLD_BERRY     = registerBerryBlock(OreBerry.create("gold", OreBerry.Placement.CEILING, 1));
	public static final OreBerry COPPER_BERRY   = registerBerryBlock(OreBerry.create("copper", OreBerry.Placement.SIDE, 3));
	public static final OreBerry SCULK_BERRY    = registerBerryBlock(OreBerry.create("sculk", OreBerry.Placement.CEILING, 1));
	public static final OreBerry AMETHYST_BERRY = registerBerryBlock(OreBerry.create("amethyst", OreBerry.Placement.FLOOR, 1));
	public static final OreBerry QUARTZ_BERRY   = registerBerryBlock(OreBerry.create("quartz", OreBerry.Placement.FLOOR, 1));
	public static final OreBerry DIAMOND_BERRY  = registerBerryBlock(OreBerry.create("diamond", OreBerry.Placement.FLOOR, 1));
	public static final OreBerry EMERALD_BERRY  = registerBerryBlock(OreBerry.create("emerald", OreBerry.Placement.FLOOR, 1));
	public static final OreBerry SCRAPS_BERRY   = registerBerryBlock(OreBerry.create("scraps", OreBerry.Placement.FLOOR, 1));

	public static final FoodComponent ORE_BERRIES = new FoodComponent.Builder().hunger(2).alwaysEdible().snack().build();

	public static final Item IRON_BERRIES     = registerBerriesItem(IRON_BERRY);
	public static final Item GOLD_BERRIES     = registerBerriesItem(GOLD_BERRY);
	public static final Item COPPER_BERRIES   = registerBerriesItem(COPPER_BERRY);
	public static final Item SCULK_BERRIES    = registerBerriesItem(SCULK_BERRY);
	public static final Item AMETHYST_BERRIES = registerBerriesItem(AMETHYST_BERRY);
	public static final Item QUARTZ_BERRIES   = registerBerriesItem(QUARTZ_BERRY);
	public static final Item DIAMOND_BERRIES  = registerBerriesItem(DIAMOND_BERRY);
	public static final Item EMERALD_BERRIES  = registerBerriesItem(EMERALD_BERRY);
	public static final Item SCRAPS_BERRIES   = registerBerriesItem(SCRAPS_BERRY);

	public static final Item IRON_BERRY_BUSH     = registerBerryBushItem(IRON_BERRY);
	public static final Item GOLD_BERRY_BUSH     = registerBerryBushItem(GOLD_BERRY);
	public static final Item COPPER_BERRY_BUSH   = registerBerryBushItem(COPPER_BERRY);
	public static final Item SCULK_BERRY_BUSH    = registerBerryBushItem(SCULK_BERRY);
	public static final Item AMETHYST_BERRY_BUSH = registerBerryBushItem(AMETHYST_BERRY);
	public static final Item QUARTZ_BERRY_BUSH   = registerBerryBushItem(QUARTZ_BERRY);
	public static final Item DIAMOND_BERRY_BUSH  = registerBerryBushItem(DIAMOND_BERRY);
	public static final Item EMERALD_BERRY_BUSH  = registerBerryBushItem(EMERALD_BERRY);
	public static final Item SCRAPS_BERRY_BUSH   = registerBerryBushItem(SCRAPS_BERRY);
	
	/*
	  so far:
	  
	  Iron:     Rustroot  (on the top)
	  Gold:     Gildberry (hanging)
	  Copper:   Coppercap (on the side)
	  Skulk/xp: Skulkvine (hanging)
	  Amethyst: 
	  Quartz:   
	  Diamond:  
	  Emerald:  
	  Scraps:   
	  
	  
	 */
	/** */
	@Override
	public void onInitialize() {
		
	}
	
	public static Identifier identifier(String identifier){
		return new Identifier("oreberry", identifier);
	}
	
	private static OreBerry registerBerryBlock(OreBerry oreBerry) {
		return Registry.register(Registry.BLOCK, identifier(oreBerry.getType() + "_berry_bush"), oreBerry);
	}

	private static Item registerBerryBushItem(OreBerry oreBerry) {
		return registerItem(oreBerry.getType() + "_berry_bush", new OreBerryBushItem(oreBerry, new Item.Settings().group(ItemGroup.DECORATIONS)));
	}

	private static Item registerBerriesItem(OreBerry oreBerry) {
		return registerItem(oreBerry.getType() + "_berries", new Item(new Item.Settings().group(ItemGroup.FOOD).food(ORE_BERRIES)));
	}

	private static Item registerItem(String id, Item item) {
		if (item instanceof BlockItem) {
			((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registry.ITEM, identifier(id), item);
	}

}
