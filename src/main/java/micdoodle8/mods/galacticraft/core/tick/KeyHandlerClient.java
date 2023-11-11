package micdoodle8.mods.galacticraft.core.tick;

import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.*;
import cpw.mods.fml.common.gameevent.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class KeyHandlerClient extends KeyHandler
{
    public static KeyBinding galaxyMap;
    public static KeyBinding openFuelGui;
    public static KeyBinding toggleAdvGoggles;
    public static KeyBinding accelerateKey;
    public static KeyBinding decelerateKey;
    public static KeyBinding leftKey;
    public static KeyBinding rightKey;
    public static KeyBinding upKey;
    public static KeyBinding downKey;
    public static KeyBinding spaceKey;
    public static KeyBinding leftShiftKey;
    private static Minecraft mc;
    
    public KeyHandlerClient() {
        super(new KeyBinding[] { KeyHandlerClient.galaxyMap, KeyHandlerClient.openFuelGui, KeyHandlerClient.toggleAdvGoggles }, new boolean[] { false, false, false }, getVanillaKeyBindings(), new boolean[] { false, true, true, true, true, true, true });
    }
    
    private static KeyBinding[] getVanillaKeyBindings() {
        final KeyBinding invKey = KeyHandlerClient.mc.gameSettings.keyBindInventory;
        KeyHandlerClient.accelerateKey = KeyHandlerClient.mc.gameSettings.keyBindForward;
        KeyHandlerClient.decelerateKey = KeyHandlerClient.mc.gameSettings.keyBindBack;
        KeyHandlerClient.leftKey = KeyHandlerClient.mc.gameSettings.keyBindLeft;
        KeyHandlerClient.rightKey = KeyHandlerClient.mc.gameSettings.keyBindRight;
        KeyHandlerClient.upKey = KeyHandlerClient.mc.gameSettings.keyBindForward;
        KeyHandlerClient.downKey = KeyHandlerClient.mc.gameSettings.keyBindBack;
        KeyHandlerClient.spaceKey = KeyHandlerClient.mc.gameSettings.keyBindJump;
        KeyHandlerClient.leftShiftKey = KeyHandlerClient.mc.gameSettings.keyBindSneak;
        return new KeyBinding[] { invKey, KeyHandlerClient.accelerateKey, KeyHandlerClient.decelerateKey, KeyHandlerClient.leftKey, KeyHandlerClient.rightKey, KeyHandlerClient.spaceKey, KeyHandlerClient.leftShiftKey };
    }
    
    public void keyDown(final TickEvent.Type types, final KeyBinding kb, final boolean tickEnd, final boolean isRepeat) {
        if (KeyHandlerClient.mc.thePlayer != null && tickEnd) {
            final EntityClientPlayerMP playerBase = PlayerUtil.getPlayerBaseClientFromPlayer((EntityPlayer)KeyHandlerClient.mc.thePlayer, false);
            if (playerBase == null) {
                return;
            }
            final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)playerBase);
            if (kb.getKeyCode() == KeyHandlerClient.galaxyMap.getKeyCode()) {
                if (KeyHandlerClient.mc.currentScreen == null) {
                    KeyHandlerClient.mc.thePlayer.openGui((Object)GalacticraftCore.instance, 3, (World)KeyHandlerClient.mc.theWorld, (int)KeyHandlerClient.mc.thePlayer.posX, (int)KeyHandlerClient.mc.thePlayer.posY, (int)KeyHandlerClient.mc.thePlayer.posZ);
                }
            }
            else if (kb.getKeyCode() == KeyHandlerClient.openFuelGui.getKeyCode()) {
                if (playerBase.ridingEntity instanceof EntitySpaceshipBase || playerBase.ridingEntity instanceof EntityBuggy) {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_OPEN_FUEL_GUI, new Object[] { playerBase.getGameProfile().getName() }));
                }
            }
            else if (kb.getKeyCode() == KeyHandlerClient.toggleAdvGoggles.getKeyCode() && playerBase != null) {
                stats.usingAdvancedGoggles = !stats.usingAdvancedGoggles;
            }
        }
        if (KeyHandlerClient.mc.thePlayer != null && KeyHandlerClient.mc.currentScreen == null) {
            int keyNum = -1;
            if (kb == KeyHandlerClient.accelerateKey) {
                keyNum = 0;
            }
            else if (kb == KeyHandlerClient.decelerateKey) {
                keyNum = 1;
            }
            else if (kb == KeyHandlerClient.leftKey) {
                keyNum = 2;
            }
            else if (kb == KeyHandlerClient.rightKey) {
                keyNum = 3;
            }
            else if (kb == KeyHandlerClient.spaceKey) {
                keyNum = 4;
            }
            else if (kb == KeyHandlerClient.leftShiftKey) {
                keyNum = 5;
            }
            final Entity entityTest = KeyHandlerClient.mc.thePlayer.ridingEntity;
            if (entityTest != null && entityTest instanceof IControllableEntity && keyNum != -1) {
                final IControllableEntity entity = (IControllableEntity)entityTest;
                if (kb.getKeyCode() == KeyHandlerClient.mc.gameSettings.keyBindInventory.getKeyCode()) {
                    KeyBinding.setKeyBindState(KeyHandlerClient.mc.gameSettings.keyBindInventory.getKeyCode(), false);
                }
                entity.pressKey(keyNum);
            }
            else if (entityTest != null && entityTest instanceof EntityAutoRocket) {
                final EntityAutoRocket autoRocket = (EntityAutoRocket)entityTest;
                if (autoRocket.landing) {
                    if (kb == KeyHandlerClient.leftShiftKey) {
                        final EntityAutoRocket entityAutoRocket = autoRocket;
                        entityAutoRocket.motionY -= 0.02;
                        GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y, new Object[] { autoRocket.getEntityId(), false }));
                    }
                    if (kb == KeyHandlerClient.spaceKey) {
                        final EntityAutoRocket entityAutoRocket2 = autoRocket;
                        entityAutoRocket2.motionY += 0.02;
                        GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y, new Object[] { autoRocket.getEntityId(), true }));
                    }
                }
            }
        }
    }
    
    public void keyUp(final TickEvent.Type types, final KeyBinding kb, final boolean tickEnd) {
    }
    
    static {
        KeyHandlerClient.galaxyMap = new KeyBinding(GCCoreUtil.translate("keybind.map.name"), (ConfigManagerCore.keyOverrideMapI == 0) ? 50 : ConfigManagerCore.keyOverrideMapI, "Galacticraft");
        KeyHandlerClient.openFuelGui = new KeyBinding(GCCoreUtil.translate("keybind.spaceshipinv.name"), (ConfigManagerCore.keyOverrideFuelLevelI == 0) ? 33 : ConfigManagerCore.keyOverrideFuelLevelI, "Galacticraft");
        KeyHandlerClient.toggleAdvGoggles = new KeyBinding(GCCoreUtil.translate("keybind.sensortoggle.name"), (ConfigManagerCore.keyOverrideToggleAdvGogglesI == 0) ? 37 : ConfigManagerCore.keyOverrideToggleAdvGogglesI, "Galacticraft");
        KeyHandlerClient.mc = Minecraft.getMinecraft();
    }
}
