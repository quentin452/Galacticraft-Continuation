package micdoodle8.mods.galacticraft.api.recipe;

public abstract class SchematicPage implements ISchematicPage
{
    public int compareTo(final ISchematicPage o) {
        if (this.getPageID() > o.getPageID()) {
            return 1;
        }
        return -1;
    }
}
