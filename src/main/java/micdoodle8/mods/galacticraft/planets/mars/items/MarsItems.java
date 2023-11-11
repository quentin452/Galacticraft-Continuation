package micdoodle8.mods.galacticraft.planets.mars.items;

import net.minecraft.item.*;
import cpw.mods.fml.common.registry.*;
import net.minecraftforge.common.util.*;

public class MarsItems
{
    public static Item marsItemBasic;
    public static Item deshPickaxe;
    public static Item deshPickSlime;
    public static Item deshAxe;
    public static Item deshHoe;
    public static Item deshSpade;
    public static Item deshSword;
    public static Item deshHelmet;
    public static Item deshChestplate;
    public static Item deshLeggings;
    public static Item deshBoots;
    public static Item spaceship;
    public static Item key;
    public static Item schematic;
    public static Item carbonFragments;
    public static Item bucketSludge;
    public static ItemArmor.ArmorMaterial ARMORDESH;
    public static Item.ToolMaterial TOOLDESH;
    
    public static void initItems() {
        MarsItems.marsItemBasic = (Item)new ItemBasicMars();
        MarsItems.deshPickaxe = new ItemPickaxeMars(MarsItems.TOOLDESH).setUnlocalizedName("deshPick");
        MarsItems.deshPickSlime = new ItemPickaxeStickyMars(MarsItems.TOOLDESH).setUnlocalizedName("deshPickSlime");
        MarsItems.deshAxe = new ItemAxeMars(MarsItems.TOOLDESH).setUnlocalizedName("deshAxe");
        MarsItems.deshHoe = new ItemHoeMars(MarsItems.TOOLDESH).setUnlocalizedName("deshHoe");
        MarsItems.deshSpade = new ItemSpadeMars(MarsItems.TOOLDESH).setUnlocalizedName("deshSpade");
        MarsItems.deshSword = new ItemSwordMars(MarsItems.TOOLDESH).setUnlocalizedName("deshSword");
        MarsItems.deshHelmet = new ItemArmorMars(MarsItems.ARMORDESH, 7, 0).setUnlocalizedName("deshHelmet");
        MarsItems.deshChestplate = new ItemArmorMars(MarsItems.ARMORDESH, 7, 1).setUnlocalizedName("deshChestplate");
        MarsItems.deshLeggings = new ItemArmorMars(MarsItems.ARMORDESH, 7, 2).setUnlocalizedName("deshLeggings");
        MarsItems.deshBoots = new ItemArmorMars(MarsItems.ARMORDESH, 7, 3).setUnlocalizedName("deshBoots");
        MarsItems.spaceship = new ItemTier2Rocket().setUnlocalizedName("spaceshipTier2");
        MarsItems.key = new ItemKeyMars().setUnlocalizedName("key");
        MarsItems.schematic = new ItemSchematicTier2().setUnlocalizedName("schematic");
        MarsItems.carbonFragments = new ItemCarbonFragments().setUnlocalizedName("carbonFragments");
        registerItems();
        registerHarvestLevels();
    }
    
    public static void registerHarvestLevels() {
        MarsItems.deshPickaxe.setHarvestLevel("pickaxe", 4);
        MarsItems.deshPickSlime.setHarvestLevel("pickaxe", 4);
        MarsItems.deshAxe.setHarvestLevel("axe", 4);
        MarsItems.deshSpade.setHarvestLevel("shovel", 4);
    }
    
    private static void registerItems() {
        registerItem(MarsItems.carbonFragments);
        registerItem(MarsItems.marsItemBasic);
        registerItem(MarsItems.deshPickaxe);
        registerItem(MarsItems.deshPickSlime);
        registerItem(MarsItems.deshAxe);
        registerItem(MarsItems.deshHoe);
        registerItem(MarsItems.deshSpade);
        registerItem(MarsItems.deshSword);
        registerItem(MarsItems.deshHelmet);
        registerItem(MarsItems.deshChestplate);
        registerItem(MarsItems.deshLeggings);
        registerItem(MarsItems.deshBoots);
        registerItem(MarsItems.spaceship);
        registerItem(MarsItems.key);
        registerItem(MarsItems.schematic);
    }
    
    public static void registerItem(final Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName(), "GalacticraftMars");
    }
    
    static {
        MarsItems.ARMORDESH = EnumHelper.addArmorMaterial("DESH", 42, new int[] { 4, 9, 7, 4 }, 12);
        MarsItems.TOOLDESH = EnumHelper.addToolMaterial("DESH", 3, 1024, 5.0f, 2.5f, 8);
    }
}
