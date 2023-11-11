package micdoodle8.mods.galacticraft.api;

import java.lang.reflect.*;

public class GalacticraftConfigAccess
{
    private static Field quickMode;
    private static Field hardMode;
    private static Field adventureMode;
    private static Field adventureRecipes;
    private static Field adventureMobDropsAndSpawning;
    private static Field adventureSpawnHandling;
    private static Field adventureAsteroidPopulation;
    
    public static boolean getQuickMode() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.quickMode.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getHardMode() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.hardMode.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getChallengeMode() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.adventureMode.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getChallengeRecipes() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.adventureRecipes.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getChallengeMobDropsAndSpawning() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.adventureMobDropsAndSpawning.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getChallengeSpawnHandling() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.adventureSpawnHandling.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean getChallengeAsteroidPopulation() {
        if (GalacticraftConfigAccess.quickMode == null) {
            setup();
        }
        try {
            return GalacticraftConfigAccess.adventureAsteroidPopulation.getBoolean(null);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private static void setup() {
        try {
            final Class<?> GCConfig = Class.forName("micdoodle8.mods.galacticraft.core.util.ConfigManagerCore");
            GalacticraftConfigAccess.quickMode = GCConfig.getField("quickMode");
            GalacticraftConfigAccess.hardMode = GCConfig.getField("hardMode");
            GalacticraftConfigAccess.adventureMode = GCConfig.getField("challengeMode");
            GalacticraftConfigAccess.adventureRecipes = GCConfig.getField("challengeRecipes");
            GalacticraftConfigAccess.adventureMobDropsAndSpawning = GCConfig.getField("challengeMobDropsAndSpawning");
            GalacticraftConfigAccess.adventureSpawnHandling = GCConfig.getField("challengeSpawnHandling");
            GalacticraftConfigAccess.adventureAsteroidPopulation = GCConfig.getField("challengeAsteroidPopulation");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
