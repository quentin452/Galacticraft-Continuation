package micdoodle8.mods.galacticraft.api.entity;

import net.minecraft.item.*;

public interface ICargoEntity
{
    EnumCargoLoadingState addCargo(final ItemStack p0, final boolean p1);
    
    RemovalResult removeCargo(final boolean p0);
    
    public enum EnumCargoLoadingState
    {
        FULL, 
        EMPTY, 
        NOTARGET, 
        NOINVENTORY, 
        SUCCESS;
    }
    
    public static class RemovalResult
    {
        public final EnumCargoLoadingState resultState;
        public final ItemStack resultStack;
        
        public RemovalResult(final EnumCargoLoadingState resultState, final ItemStack resultStack) {
            this.resultState = resultState;
            this.resultStack = resultStack;
        }
    }
}
