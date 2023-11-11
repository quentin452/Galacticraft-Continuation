package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.entity.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.registry.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import net.minecraft.launchwrapper.*;
import net.minecraft.inventory.*;

public class GCCoreUtil
{
    public static int nextID;
    private static boolean deobfuscated;
    
    public static boolean isDeobfuscated() {
        return GCCoreUtil.deobfuscated;
    }
    
    public static void openBuggyInv(final EntityPlayerMP player, final IInventory buggyInv, final int type) {
        player.getNextWindowId();
        player.closeContainer();
        final int id = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI, new Object[] { id, 0, 0 }), player);
        player.openContainer = (Container)new ContainerBuggy((IInventory)player.inventory, buggyInv, type);
        player.openContainer.windowId = id;
        player.openContainer.addCraftingToCrafters((ICrafting)player);
    }
    
    public static void openParachestInv(final EntityPlayerMP player, final EntityLanderBase landerInv) {
        player.getNextWindowId();
        player.closeContainer();
        final int windowId = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI, new Object[] { windowId, 1, landerInv.getEntityId() }), player);
        player.openContainer = (Container)new ContainerParaChest((IInventory)player.inventory, (IInventory)landerInv);
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters((ICrafting)player);
    }
    
    public static int nextInternalID() {
        ++GCCoreUtil.nextID;
        return GCCoreUtil.nextID - 1;
    }
    
    public static void registerGalacticraftCreature(final Class<? extends Entity> var0, final String var1, final int back, final int fore) {
        registerGalacticraftNonMobEntity(var0, var1, 80, 3, true);
        final int nextEggID = getNextValidEggID();
        if (nextEggID < 65536) {
            EntityList.IDtoClassMapping.put(nextEggID, var0);
            VersionUtil.putClassToIDMapping(var0, nextEggID);
            EntityList.entityEggs.put(nextEggID, new EntityList.EntityEggInfo(nextEggID, back, fore));
        }
    }
    
    public static int getNextValidEggID() {
        int eggID = 255;
        while (EntityList.getClassFromID(++eggID) != null) {}
        return eggID;
    }
    
    public static void registerGalacticraftNonMobEntity(final Class<? extends Entity> var0, final String var1, final int trackingDistance, final int updateFreq, final boolean sendVel) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            LanguageRegistry.instance().addStringLocalization("entity.GalacticraftCore." + var1 + ".name", translate("entity." + var1 + ".name"));
        }
        EntityRegistry.registerModEntity((Class)var0, var1, nextInternalID(), (Object)GalacticraftCore.instance, trackingDistance, updateFreq, sendVel);
    }
    
    public static void registerGalacticraftItem(final String key, final Item item) {
        GalacticraftCore.itemList.put(key, new ItemStack(item));
    }
    
    public static void registerGalacticraftItem(final String key, final Item item, final int metadata) {
        GalacticraftCore.itemList.put(key, new ItemStack(item, 1, metadata));
    }
    
    public static void registerGalacticraftItem(final String key, final ItemStack stack) {
        GalacticraftCore.itemList.put(key, stack);
    }
    
    public static void registerGalacticraftBlock(final String key, final Block block) {
        GalacticraftCore.blocksList.put(key, new ItemStack(block));
    }
    
    public static void registerGalacticraftBlock(final String key, final Block block, final int metadata) {
        GalacticraftCore.blocksList.put(key, new ItemStack(block, 1, metadata));
    }
    
    public static void registerGalacticraftBlock(final String key, final ItemStack stack) {
        GalacticraftCore.blocksList.put(key, stack);
    }
    
    public static String translate(final String key) {
        final String result = StatCollector.translateToLocal(key);
        final int comment = result.indexOf(35);
        return (comment > 0) ? result.substring(0, comment).trim() : result;
    }
    
    public static List<String> translateWithSplit(final String key) {
        String translated = translate(key);
        final int comment = translated.indexOf(35);
        translated = ((comment > 0) ? translated.substring(0, comment).trim() : translated);
        return Arrays.asList(translated.split("\\$"));
    }
    
    public static String translateWithFormat(final String key, final Object... values) {
        final String result = StatCollector.translateToLocalFormatted(key, values);
        final int comment = result.indexOf(35);
        return (comment > 0) ? result.substring(0, comment).trim() : result;
    }
    
    public static void drawStringRightAligned(final String string, final int x, final int y, final int color, final FontRenderer fontRendererObj) {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string), y, color);
    }
    
    public static void drawStringCentered(final String string, final int x, final int y, final int color, final FontRenderer fontRendererObj) {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string) / 2, y, color);
    }
    
    public static String lowerCaseNoun(final String string) {
        final Language l = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        if (l.getLanguageCode().equals("de_DE")) {
            return string;
        }
        return translate(string).toLowerCase();
    }
    
    static {
        GCCoreUtil.nextID = 0;
        try {
            GCCoreUtil.deobfuscated = (Launch.classLoader.getClassBytes("net.minecraft.world.World") != null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
