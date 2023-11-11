package micdoodle8.mods.galacticraft.core.world.gen;

public class StructureVillagePieceWeightMoon
{
    public Class<? extends StructureComponentVillage> villagePieceClass;
    public final int villagePieceWeight;
    public int villagePiecesSpawned;
    public int villagePiecesLimit;
    
    public StructureVillagePieceWeightMoon(final Class<? extends StructureComponentVillage> par1Class, final int par2, final int par3) {
        this.villagePieceClass = par1Class;
        this.villagePieceWeight = par2;
        this.villagePiecesLimit = (int)(par3 / 1.5);
    }
    
    public boolean canSpawnMoreVillagePiecesOfType(final int par1) {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }
    
    public boolean canSpawnMoreVillagePieces() {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }
}
