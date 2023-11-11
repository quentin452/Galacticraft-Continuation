package micdoodle8.mods.galacticraft.core.recipe.craftguide;

import java.lang.reflect.*;

public class CraftGuideIntegration
{
    public static void register() {
        try {
            final Class c = Class.forName("uristqwerty.CraftGuide.ReflectionAPI");
            final Method m = c.getMethod("registerAPIObject", Object.class);
            m.invoke(null, new CraftGuideCompressorRecipes());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
