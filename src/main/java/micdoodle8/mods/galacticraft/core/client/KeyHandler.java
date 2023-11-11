package micdoodle8.mods.galacticraft.core.client;

import net.minecraft.client.settings.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.*;
import org.lwjgl.input.*;

public abstract class KeyHandler
{
    private final KeyBinding[] keyBindings;
    private KeyBinding[] vKeyBindings;
    private boolean[] keyDown;
    private boolean[] repeatings;
    private boolean[] vRepeatings;
    public boolean isDummy;
    
    public KeyHandler(final KeyBinding[] keyBindings, final boolean[] repeatings, final KeyBinding[] vanillaKeys, final boolean[] vanillaRepeatings) {
        assert keyBindings.length == repeatings.length : "You need to pass two arrays of identical length";
        assert vanillaKeys.length == vanillaRepeatings.length : "You need to pass two arrays of identical length";
        this.keyBindings = keyBindings;
        this.repeatings = repeatings;
        this.vKeyBindings = vanillaKeys;
        this.vRepeatings = vanillaRepeatings;
        this.keyDown = new boolean[keyBindings.length + vanillaKeys.length];
    }
    
    public KeyHandler(final KeyBinding[] keyBindings) {
        this.keyBindings = keyBindings;
        this.isDummy = true;
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT) {
            if (event.phase == TickEvent.Phase.START) {
                this.keyTick(event.type, false);
            }
            else if (event.phase == TickEvent.Phase.END) {
                this.keyTick(event.type, true);
            }
        }
    }
    
    public void keyTick(final TickEvent.Type type, final boolean tickEnd) {
        final boolean inChat = FMLClientHandler.instance().getClient().currentScreen instanceof GuiChat;
        for (int i = 0; i < this.keyBindings.length; ++i) {
            final KeyBinding keyBinding = this.keyBindings[i];
            final int keyCode = keyBinding.getKeyCode();
            if (keyCode != 0) {
                final boolean state = !inChat && ((keyCode < 0) ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
                if (state != this.keyDown[i] || (state && this.repeatings[i])) {
                    if (state) {
                        this.keyDown(type, keyBinding, tickEnd, state != this.keyDown[i]);
                    }
                    else {
                        this.keyUp(type, keyBinding, tickEnd);
                    }
                    if (tickEnd) {
                        this.keyDown[i] = state;
                    }
                }
            }
        }
        for (int i = 0; i < this.vKeyBindings.length; ++i) {
            final KeyBinding keyBinding = this.vKeyBindings[i];
            final int keyCode = keyBinding.getKeyCode();
            if (keyCode != 0) {
                final boolean state = (keyCode < 0) ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
                if (state != this.keyDown[i + this.keyBindings.length] || (state && this.vRepeatings[i])) {
                    if (state) {
                        this.keyDown(type, keyBinding, tickEnd, state != this.keyDown[i + this.keyBindings.length]);
                    }
                    else {
                        this.keyUp(type, keyBinding, tickEnd);
                    }
                    if (tickEnd) {
                        this.keyDown[i + this.keyBindings.length] = state;
                    }
                }
            }
        }
    }
    
    public abstract void keyDown(final TickEvent.Type p0, final KeyBinding p1, final boolean p2, final boolean p3);
    
    public abstract void keyUp(final TickEvent.Type p0, final KeyBinding p1, final boolean p2);
}
