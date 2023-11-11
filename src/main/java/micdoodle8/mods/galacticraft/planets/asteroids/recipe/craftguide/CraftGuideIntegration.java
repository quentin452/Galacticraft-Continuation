package micdoodle8.mods.galacticraft.planets.asteroids.recipe.craftguide;

public class CraftGuideIntegration
{
    public static void register() {
        try {
            final Class c = Class.forName("uristqwerty.CraftGuide.ReflectionAPI");
            c.getMethod("registerAPIObject", Object.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
