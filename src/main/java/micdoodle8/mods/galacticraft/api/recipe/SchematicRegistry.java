package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraft.item.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.relauncher.*;

public class SchematicRegistry
{
    public static ArrayList<ISchematicPage> schematicRecipes;
    
    public static void registerSchematicRecipe(final ISchematicPage page) {
        if (!SchematicRegistry.schematicRecipes.contains(page)) {
            SchematicRegistry.schematicRecipes.add(page);
        }
    }
    
    public static ISchematicPage getMatchingRecipeForItemStack(final ItemStack stack) {
        for (final ISchematicPage schematic : SchematicRegistry.schematicRecipes) {
            final ItemStack requiredItem = schematic.getRequiredItem();
            if (requiredItem != null && stack != null && requiredItem.isItemEqual(stack)) {
                return schematic;
            }
        }
        return null;
    }
    
    public static ISchematicPage getMatchingRecipeForID(final int id) {
        for (final ISchematicPage schematic : SchematicRegistry.schematicRecipes) {
            if (schematic.getPageID() == id) {
                return schematic;
            }
        }
        return null;
    }
    
    public static void addUnlockedPage(final EntityPlayerMP player, final ISchematicPage page) {
        if (page != null) {
            MinecraftForge.EVENT_BUS.post((Event)new SchematicEvent.Unlock(player, page));
        }
    }
    
    public static ISchematicPage unlockNewPage(final EntityPlayerMP player, final ItemStack stack) {
        if (stack != null) {
            final ISchematicPage schematic = getMatchingRecipeForItemStack(stack);
            if (schematic != null) {
                addUnlockedPage(player, schematic);
                return schematic;
            }
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public static void flipToNextPage(final int currentIndex) {
        FMLClientHandler.instance().getClient().currentScreen = null;
        MinecraftForge.EVENT_BUS.post((Event)new SchematicEvent.FlipPage((ISchematicPage)null, currentIndex, 1));
    }
    
    @SideOnly(Side.CLIENT)
    public static void flipToLastPage(final int currentIndex) {
        FMLClientHandler.instance().getClient().currentScreen = null;
        MinecraftForge.EVENT_BUS.post((Event)new SchematicEvent.FlipPage((ISchematicPage)null, currentIndex, -1));
    }
    
    static {
        SchematicRegistry.schematicRecipes = new ArrayList<ISchematicPage>();
    }
}
