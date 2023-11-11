package micdoodle8.mods.miccore;

import java.lang.reflect.*;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.versioning.*;
import javax.swing.event.*;
import java.awt.*;
import net.minecraft.launchwrapper.*;
import org.apache.commons.io.*;
import java.util.*;
import cpw.mods.fml.common.*;
import java.io.*;
import java.util.List;
import java.util.zip.*;
import javax.swing.*;
import java.net.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;

@IFMLLoadingPlugin.TransformerExclusions({ "micdoodle8.mods.miccore" })
public class MicdoodlePlugin implements IFMLLoadingPlugin, IFMLCallHook
{
    public static boolean hasRegistered;
    public static final String mcVersion = "[1.7.2],[1.7.10]";
    public static File mcDir;
    public static File canonicalConfigDir;
    private static boolean checkedVersions;
    private static Constructor<?> sleepCancelledConstructor;
    private static Constructor<?> orientCameraConstructor;
    private static String galacticraftCoreClass;

    public MicdoodlePlugin() {
        DepLoader.load();
    }

    public static void versionCheck(final String reqVersion, final String mod) {
        final String mcVersion = (String)FMLInjectionData.data()[4];
        if (!VersionParser.parseRange(reqVersion).containsVersion((ArtifactVersion)new DefaultArtifactVersion(mcVersion))) {
            final String err = "This version of " + mod + " does not support minecraft version " + mcVersion;
            System.err.println(err);
            final JEditorPane ep = new JEditorPane("text/html", "<html>" + err + "<br>Remove it from your mods folder and check <a href=\"http://micdoodle8.com\">here</a> for updates</html>");
            ep.setEditable(false);
            ep.setOpaque(false);
            ep.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(final HyperlinkEvent event) {
                    try {
                        if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                            Desktop.getDesktop().browse(event.getURL().toURI());
                        }
                    }
                    catch (Exception ex) {}
                }
            });
            JOptionPane.showMessageDialog(null, ep, "Fatal error", 0);
            System.exit(1);
        }
    }

    public String[] getASMTransformerClass() {
        versionCheck("[1.7.2],[1.7.10]", "MicdoodleCore");
        final String[] asmStrings = { "micdoodle8.mods.miccore.MicdoodleTransformer" };
        if (!MicdoodlePlugin.hasRegistered) {
            final List<String> asm = Arrays.asList(asmStrings);
            for (final String s : asm) {
                try {
                    final Class<?> c = Class.forName(s);
                    if (c == null) {
                        continue;
                    }
                    System.out.println("Successfully Registered Transformer");
                }
                catch (Exception ex) {
                    System.out.println("Error while running transformer " + s);
                    return null;
                }
            }
            MicdoodlePlugin.hasRegistered = true;
        }
        return asmStrings;
    }

    public String getModContainerClass() {
        return "micdoodle8.mods.miccore.MicdoodleModContainer";
    }

    public String getSetupClass() {
        return "micdoodle8.mods.miccore.MicdoodlePlugin";
    }

    public void injectData(final Map<String, Object> data) {
        if (data.containsKey("mcLocation")) {
            MicdoodlePlugin.mcDir = (File) data.get("mcLocation");
            final File configDir = new File(MicdoodlePlugin.mcDir, "config");
            final File modsDir = new File(MicdoodlePlugin.mcDir, "mods");
            final String minecraftVersion = (String)FMLInjectionData.data()[4];
            final File subDir = new File(modsDir, minecraftVersion);
            if (!MicdoodlePlugin.checkedVersions) {
                MicdoodlePlugin.checkedVersions = true;
                boolean obfuscated = false;
                try {
                    obfuscated = (Launch.classLoader.getClassBytes("net.minecraft.world.World") == null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (obfuscated) {
                    final Collection<File> fileList = (Collection<File>)FileUtils.listFiles(modsDir, new String[] { "jar", "zip" }, false);
                    String[] micCoreVersion = null;
                    String[] gcVersion = null;
                    if (fileList != null) {
                        if (subDir.isDirectory()) {
                            fileList.addAll(FileUtils.listFiles(subDir, new String[] { "jar", "zip" }, false));
                        }
                        for (final File file : fileList) {
                            if (file.getName().toLowerCase().contains("dragonapi") && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
                                this.jarIntegrityCheck(file, true);
                            }
                            if (file.getName().toLowerCase().contains("micdoodlecore")) {
                                final String fileName = file.getName().toLowerCase();
                                final String[] split0 = fileName.split("\\-");
                                if (split0.length == 4) {
                                    final String micVersion = split0[3].replace(".jar", "").replace(".zip", "");
                                    micCoreVersion = micVersion.split("\\.");
                                }
                                else if (split0.length == 3) {
                                    final String micVersion = split0[2].replace(".jar", "").replace(".zip", "");
                                    micCoreVersion = micVersion.split("\\.");
                                }
                                if (!this.jarIntegrityCheck(file, false)) {
                                    showErrorDialog(new Object[] { "Re-download", "Ignore" }, "Mod file " + file.getName() + " is an incomplete download and will likely cause errors, please re-download it!");
                                }
                            }
                            if (file.getName().toLowerCase().contains("galacticraftcore")) {
                                final String fileName = file.getName().toLowerCase();
                                final String[] split0 = fileName.split("\\-");
                                if (split0.length == 4) {
                                    final String micVersion = split0[3].replace(".jar", "").replace(".zip", "");
                                    gcVersion = micVersion.split("\\.");
                                }
                                else if (split0.length == 3) {
                                    final String micVersion = split0[2].replace(".jar", "").replace(".zip", "");
                                    gcVersion = micVersion.split("\\.");
                                }
                                if (!this.jarIntegrityCheck(file, false)) {
                                    showErrorDialog(new Object[] { "Re-download", "Ignore" }, "Mod file " + file.getName() + " is an incomplete download and will likely cause errors, please re-download it!");
                                }
                            }
                            if (file.getName().toLowerCase().contains("galacticraft-planets") && !this.jarIntegrityCheck(file, false)) {
                                showErrorDialog(new Object[] { "Re-download", "Ignore" }, "Mod file " + file.getName() + " is an incomplete download and will likely cause errors, please re-download it!");
                            }
                        }
                    }
                    if (micCoreVersion == null) {
                        FMLLog.info("Failed to find MicdoodleCore file in mods folder, skipping GC version check.", new Object[0]);
                    }
                    else if (gcVersion == null) {
                        showErrorDialog(new Object[] { "Install", "Ignore" }, "Failed to find Galacticraft file in mods folder!");
                    }
                    else if (micCoreVersion.length != gcVersion.length) {
                        showErrorDialog(new Object[] { "Reinstall", "Ignore" }, "Failed to match Galacticraft version to MicdoodleCore version!");
                    }
                    else {
                        for (int i = 0; i < (micCoreVersion.length & gcVersion.length); ++i) {
                            micCoreVersion[i] = this.trimInvalidIntegers(micCoreVersion[i]);
                            gcVersion[i] = this.trimInvalidIntegers(gcVersion[i]);
                        }
                        for (int i = 0; i < micCoreVersion.length; ++i) {
                            if (!micCoreVersion[i].equals(gcVersion[i])) {
                                final int micCoreVersionI = Integer.parseInt(micCoreVersion[i]);
                                final int gcVersionI = Integer.parseInt(gcVersion[i]);
                                if (micCoreVersionI < gcVersionI) {
                                    showErrorDialog(new Object[] { "Update", "Ignore" }, "MicdoodleCore Update Required!", "Galacticraft and MicdoodleCore should always be at the same version", "Severe issues can be caused from not updating");
                                }
                                else {
                                    showErrorDialog(new Object[] { "Update", "Ignore" }, "Galacticraft Update Required!", "Galacticraft and MicdoodleCore should always be at the same version", "Severe issues can be caused from not updating");
                                }
                            }
                        }
                    }
                }
            }
            String canonicalConfigPath;
            try {
                canonicalConfigPath = configDir.getCanonicalPath();
                MicdoodlePlugin.canonicalConfigDir = configDir.getCanonicalFile();
            }
            catch (IOException ioe) {
                throw new LoaderException((Throwable)ioe);
            }
            if (!MicdoodlePlugin.canonicalConfigDir.exists()) {
                FMLLog.fine("No config directory found, creating one: %s", new Object[] { canonicalConfigPath });
                final boolean dirMade = MicdoodlePlugin.canonicalConfigDir.mkdir();
                if (!dirMade) {
                    FMLLog.severe("Unable to create the config directory %s", new Object[] { canonicalConfigPath });
                    throw new LoaderException();
                }
                FMLLog.info("Config directory created successfully", new Object[0]);
            }
            ConfigManagerMicCore.init();
        }
        System.out.println("[Micdoodle8Core]: Patching game...");
    }

    private boolean jarIntegrityCheck(final File file, final boolean checkDragonAPI) {
        ZipFile zipfile = null;
        ZipInputStream zis = null;
        try {
            zipfile = new ZipFile(file);
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze = zis.getNextEntry();
            if (ze == null) {
                return false;
            }
            while (ze != null) {
                zipfile.getInputStream(ze);
                ze.getCrc();
                ze.getCompressedSize();
                if (checkDragonAPI && "micdoodle8/mods/galacticraft/api/".equals(ze.getName())) {
                    showErrorDialog(new Object[] { "Quit", "Ignore" }, "DragonAPI will prevent Galacticraft from working properly!", "To fix: remove or modify DragonAPI", "More info: http://wiki.micdoodle8.com/wiki/Compatibility");
                }
                else {
                    ze.getName();
                }
                ze = zis.getNextEntry();
            }
            return true;
        }
        catch (ZipException e) {
            return false;
        }
        catch (IOException e2) {
            return false;
        }
        finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                }
            }
            catch (IOException e3) {
                return false;
            }
            try {
                if (zis != null) {
                    zis.close();
                }
            }
            catch (IOException e3) {
                return false;
            }
        }
    }

    public static void showErrorDialog(final Object[] options, final String... messages) {
        try {
            String err = "<html>";
            for (final String s : messages) {
                System.err.print(s);
                err = err.concat(s + "<br />");
            }
            err = err.concat("</html>");
            final JEditorPane ep = new JEditorPane("text/html", err);
            ep.setEditable(false);
            ep.setOpaque(false);
            ep.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(final HyperlinkEvent event) {
                    try {
                        if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                            Desktop.getDesktop().browse(event.getURL().toURI());
                        }
                    }
                    catch (Exception ex) {}
                }
            });
            final int ret = JOptionPane.showOptionDialog(null, ep, "Fatal error", -1, 0, null, options, options[0]);
            System.err.println(ret);
            switch (ret) {
                case 0: {
                    try {
                        if ("Quit".equals(options[0])) {
                            Desktop.getDesktop().browse(new URL("http://wiki.micdoodle8.com/wiki/Compatibility#DragonAPI").toURI());
                        }
                        else {
                            Desktop.getDesktop().browse(new URL("http://micdoodle8.com/mods/galacticraft/downloads").toURI());
                        }
                    }
                    catch (Exception ex) {}
                    System.exit(0);
                }
            }
        }
        catch (Exception e) {
            System.out.println("ERROR: Lacking graphical display: unable to display normal error messagebox with options.");
            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.println("The error would have been:");
            for (int i = 0; i < messages.length; ++i) {
                System.out.println("    " + messages[i]);
            }
        }
    }

    private String trimInvalidIntegers(final String toTrim) {
        String newString = "";
        for (int j = 0; j < toTrim.length(); ++j) {
            final String c = toTrim.substring(j, j + 1);
            if (!"0123456789".contains(c)) {
                break;
            }
            newString = newString.concat(c);
        }
        return newString;
    }

    public Void call() throws Exception {
        return null;
    }

    public static void onSleepCancelled() {
        try {
            if (MicdoodlePlugin.sleepCancelledConstructor == null) {
                MicdoodlePlugin.sleepCancelledConstructor = Class.forName(MicdoodlePlugin.galacticraftCoreClass + "$SleepCancelledEvent").getConstructor((Class<?>[])new Class[0]);
            }
            MinecraftForge.EVENT_BUS.post((Event)MicdoodlePlugin.sleepCancelledConstructor.newInstance(new Object[0]));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void orientCamera() {
        try {
            if (MicdoodlePlugin.orientCameraConstructor == null) {
                MicdoodlePlugin.orientCameraConstructor = Class.forName(MicdoodlePlugin.galacticraftCoreClass + "$OrientCameraEvent").getConstructor((Class<?>[])new Class[0]);
            }
            MinecraftForge.EVENT_BUS.post((Event)MicdoodlePlugin.orientCameraConstructor.newInstance(new Object[0]));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccessTransformerClass() {
        boolean deobfuscated = true;
        try {
            deobfuscated = (Launch.classLoader.getClassBytes("net.minecraft.world.World") != null);
        }
        catch (Exception ex) {}
        if (deobfuscated) {
            return "micdoodle8.mods.miccore.MicdoodleAccessTransformerDeObf";
        }
        return "micdoodle8.mods.miccore.MicdoodleAccessTransformer";
    }

    static {
        MicdoodlePlugin.hasRegistered = false;
        MicdoodlePlugin.checkedVersions = false;
        MicdoodlePlugin.galacticraftCoreClass = "micdoodle8.mods.galacticraft.core.event.EventHandlerGC";
    }
}
