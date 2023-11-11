package micdoodle8.mods.galacticraft.api.galaxies;

import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import java.util.*;
import com.google.common.collect.*;

public class GalaxyRegistry
{
    static int maxSolarSystemID;
    static int maxPlanetID;
    static int maxMoonID;
    static int maxSatelliteID;
    static HashMap<String, SolarSystem> solarSystems;
    static BiMap<String, Integer> solarSystemIDs;
    static TreeMap<String, Planet> planets;
    static BiMap<String, Integer> planetIDs;
    static TreeMap<String, Moon> moons;
    static BiMap<String, Integer> moonIDs;
    static HashMap<String, Satellite> satellites;
    static BiMap<String, Integer> satelliteIDs;
    static HashMap<Planet, List<Moon>> moonList;
    static HashMap<CelestialBody, List<Satellite>> satelliteList;
    static HashMap<SolarSystem, List<Planet>> solarSystemList;

    public static CelestialBody getCelestialBodyFromDimensionID(final int dimensionID) {
        for (final Planet planet : GalaxyRegistry.planets.values()) {
            if (planet.getDimensionID() == dimensionID) {
                return planet;
            }
        }
        for (final Moon moon : GalaxyRegistry.moons.values()) {
            if (moon.getDimensionID() == dimensionID) {
                return moon;
            }
        }
        for (final Satellite satellite : GalaxyRegistry.satellites.values()) {
            if (satellite.getDimensionID() == dimensionID) {
                return satellite;
            }
        }
        return null;
    }

    public static void refreshGalaxies() {
        GalaxyRegistry.moonList.clear();
        GalaxyRegistry.satelliteList.clear();
        GalaxyRegistry.solarSystemList.clear();
        for (final Moon moon : getRegisteredMoons().values()) {
            final Planet planet = moon.getParentPlanet();
            List<Moon> listOfMoons = GalaxyRegistry.moonList.get(planet);
            if (listOfMoons == null) {
                listOfMoons = new ArrayList<Moon>();
            }
            listOfMoons.add(moon);
            GalaxyRegistry.moonList.put(planet, listOfMoons);
        }
        for (final Satellite satellite : getRegisteredSatellites().values()) {
            final CelestialBody celestialBody = satellite.getParentPlanet();
            List<Satellite> satelliteList1 = GalaxyRegistry.satelliteList.get(celestialBody);
            if (satelliteList1 == null) {
                satelliteList1 = new ArrayList<Satellite>();
            }
            satelliteList1.add(satellite);
            GalaxyRegistry.satelliteList.put(celestialBody, satelliteList1);
        }
        for (final Planet planet2 : getRegisteredPlanets().values()) {
            final SolarSystem solarSystem = planet2.getParentSolarSystem();
            List<Planet> planetList = GalaxyRegistry.solarSystemList.get(solarSystem);
            if (planetList == null) {
                planetList = new ArrayList<Planet>();
            }
            planetList.add(planet2);
            GalaxyRegistry.solarSystemList.put(solarSystem, planetList);
        }
    }

    public static List<Planet> getPlanetsForSolarSystem(final SolarSystem solarSystem) {
        final List<Planet> solarSystemListLocal = GalaxyRegistry.solarSystemList.get(solarSystem);
        if (solarSystemListLocal == null) {
            return new ArrayList<Planet>();
        }
        return (List<Planet>)ImmutableList.copyOf((Collection<Planet>)solarSystemListLocal);
    }

    public static List<Moon> getMoonsForPlanet(final Planet planet) {
        final List<Moon> moonListLocal = GalaxyRegistry.moonList.get(planet);
        if (moonListLocal == null) {
            return new ArrayList<Moon>();
        }
        return (List<Moon>)ImmutableList.copyOf((Collection<Moon>)moonListLocal);
    }

    public static List<Satellite> getSatellitesForCelestialBody(final CelestialBody celestialBody) {
        final List<Satellite> satelliteList1 = GalaxyRegistry.satelliteList.get(celestialBody);
        if (satelliteList1 == null) {
            return new ArrayList<Satellite>();
        }
        return (List<Satellite>)ImmutableList.copyOf((Collection<Satellite>)satelliteList1);
    }

    public static CelestialBody getCelestialBodyFromUnlocalizedName(final String unlocalizedName) {
        for (final Planet planet : GalaxyRegistry.planets.values()) {
            if (planet.getUnlocalizedName().equals(unlocalizedName)) {
                return planet;
            }
        }
        for (final Moon moon : GalaxyRegistry.moons.values()) {
            if (moon.getUnlocalizedName().equals(unlocalizedName)) {
                return moon;
            }
        }
        return null;
    }

    public static boolean registerSolarSystem(final SolarSystem solarSystem) {
        if (GalaxyRegistry.solarSystemIDs.containsKey((Object)solarSystem.getName())) {
            return false;
        }
        GalaxyRegistry.solarSystems.put(solarSystem.getName(), solarSystem);
        GalaxyRegistry.solarSystemIDs.put(solarSystem.getName(), ++GalaxyRegistry.maxSolarSystemID);
        MinecraftForge.EVENT_BUS.post((Event)new SolarSystemRegisterEvent(solarSystem.getName(), GalaxyRegistry.maxSolarSystemID));
        return true;
    }

    public static boolean registerPlanet(final Planet planet) {
        if (GalaxyRegistry.planetIDs.containsKey(planet.getName())) {
            return false;
        }
        GalaxyRegistry.planets.put(planet.getName(), planet);
        GalaxyRegistry.planetIDs.put(planet.getName(), ++GalaxyRegistry.maxPlanetID);
        MinecraftForge.EVENT_BUS.post(new PlanetRegisterEvent(planet.getName(), GalaxyRegistry.maxPlanetID));
        return true;
    }

    public static boolean registerMoon(final Moon moon) {
        if (GalaxyRegistry.moonIDs.containsKey(moon.getName())) {
            return false;
        }
        GalaxyRegistry.moons.put(moon.getName(), moon);
        GalaxyRegistry.moonIDs.put(moon.getName(), ++GalaxyRegistry.maxMoonID);
        MinecraftForge.EVENT_BUS.post(new MoonRegisterEvent(moon.getName(), GalaxyRegistry.maxMoonID));
        return true;
    }

    public static boolean registerSatellite(final Satellite satellite) {
        if (GalaxyRegistry.satelliteIDs.containsKey(satellite.getName())) {
            return false;
        }
        if (satellite.getParentPlanet() == null) {
            throw new RuntimeException("Registering satellite without a parent!!!");
        }
        GalaxyRegistry.satellites.put(satellite.getName(), satellite);
        GalaxyRegistry.satelliteIDs.put(satellite.getName(), ++GalaxyRegistry.maxSatelliteID);
        MinecraftForge.EVENT_BUS.post(new SatelliteRegisterEvent(satellite.getName(), GalaxyRegistry.maxSatelliteID));
        return true;
    }

    public static Map<String, SolarSystem> getRegisteredSolarSystems() {
        return ImmutableMap.copyOf(GalaxyRegistry.solarSystems);
    }

    public static Map<String, Integer> getRegisteredSolarSystemIDs() {
        return ImmutableMap.copyOf(GalaxyRegistry.solarSystemIDs);
    }

    public static Map<String, Planet> getRegisteredPlanets() {
        return (Map<String, Planet>)GalaxyRegistry.planets.clone();
    }

    public static Map<String, Integer> getRegisteredPlanetIDs() {
        return ImmutableMap.copyOf(GalaxyRegistry.planetIDs);
    }

    public static Map<String, Moon> getRegisteredMoons() {
        return (Map<String, Moon>)GalaxyRegistry.moons.clone();
    }

    public static Map<String, Integer> getRegisteredMoonIDs() {
        return ImmutableMap.copyOf(GalaxyRegistry.moonIDs);
    }

    public static Map<String, Satellite> getRegisteredSatellites() {
        return ImmutableMap.copyOf(GalaxyRegistry.satellites);
    }

    public static Map<String, Integer> getRegisteredSatelliteIDs() {
        return ImmutableMap.copyOf(GalaxyRegistry.satelliteIDs);
    }

    public static int getSolarSystemID(final String solarSystemName) {
        return GalaxyRegistry.solarSystemIDs.get(solarSystemName);
    }

    public static int getPlanetID(final String planetName) {
        return GalaxyRegistry.planetIDs.get(planetName);
    }

    public static int getMoonID(final String moonName) {
        return GalaxyRegistry.moonIDs.get(moonName);
    }

    public static int getSatelliteID(final String satelliteName) {
        return GalaxyRegistry.satelliteIDs.get(satelliteName);
    }

    static {
        GalaxyRegistry.maxSolarSystemID = 0;
        GalaxyRegistry.maxPlanetID = 0;
        GalaxyRegistry.maxMoonID = 0;
        GalaxyRegistry.maxSatelliteID = 0;
        GalaxyRegistry.solarSystems = Maps.newHashMap();
        GalaxyRegistry.solarSystemIDs = HashBiMap.create();
        GalaxyRegistry.planets = Maps.newTreeMap();
        GalaxyRegistry.planetIDs = HashBiMap.create();
        GalaxyRegistry.moons = Maps.newTreeMap();
        GalaxyRegistry.moonIDs = HashBiMap.create();
        GalaxyRegistry.satellites = Maps.newHashMap();
        GalaxyRegistry.satelliteIDs = HashBiMap.create();
        GalaxyRegistry.moonList = Maps.newHashMap();
        GalaxyRegistry.satelliteList = Maps.newHashMap();
        GalaxyRegistry.solarSystemList = Maps.newHashMap();
    }

    public static class SolarSystemRegisterEvent extends Event
    {
        public final String solarSystemName;
        public final int solarSystemID;

        public SolarSystemRegisterEvent(final String solarSystemName, final int solarSystemID) {
            this.solarSystemName = solarSystemName;
            this.solarSystemID = solarSystemID;
        }
    }

    public static class PlanetRegisterEvent extends Event
    {
        public final String planetName;
        public final int planetID;

        public PlanetRegisterEvent(final String planetName, final int planetID) {
            this.planetName = planetName;
            this.planetID = planetID;
        }
    }

    public static class MoonRegisterEvent extends Event
    {
        public final String moonName;
        public final int moonID;

        public MoonRegisterEvent(final String moonName, final int moonID) {
            this.moonName = moonName;
            this.moonID = moonID;
        }
    }

    public static class SatelliteRegisterEvent extends Event
    {
        public final String satelliteName;
        public final int satelliteID;

        public SatelliteRegisterEvent(final String satelliteName, final int satelliteID) {
            this.satelliteName = satelliteName;
            this.satelliteID = satelliteID;
        }
    }
}
