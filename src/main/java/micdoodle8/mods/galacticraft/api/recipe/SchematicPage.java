package micdoodle8.mods.galacticraft.api.recipe;

/**
 * Reference implementation of {@link ISchematicPage}. Use/extend this or implement your own.
 */
public abstract class SchematicPage implements ISchematicPage, Comparable<ISchematicPage> {

    @Override
    public int compareTo(ISchematicPage o) {
        int thisID = this.getPageID();
        int otherID = o.getPageID();

        // Compare based on page IDs
        if (thisID < otherID) {
            return -1;
        } else if (thisID > otherID) {
            return 1;
        }
        return 0; // Page IDs are equal
    }
}
