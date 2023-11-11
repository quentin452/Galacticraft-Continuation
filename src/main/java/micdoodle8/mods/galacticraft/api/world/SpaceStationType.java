package micdoodle8.mods.galacticraft.api.world;

import micdoodle8.mods.galacticraft.api.recipe.*;

public class SpaceStationType
{
    private final int spaceStationID;
    private final int planetID;
    private final SpaceStationRecipe recipe;
    
    public SpaceStationType(final int spaceStationID, final int planetID, final SpaceStationRecipe recipe) {
        this.spaceStationID = spaceStationID;
        this.planetID = planetID;
        this.recipe = recipe;
    }
    
    public int getSpaceStationID() {
        return this.spaceStationID;
    }
    
    public int getWorldToOrbitID() {
        return this.planetID;
    }
    
    public SpaceStationRecipe getRecipeForSpaceStation() {
        return this.recipe;
    }
}
