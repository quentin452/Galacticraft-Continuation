package micdoodle8.mods.galacticraft.core.util;

import cpw.mods.fml.common.*;
import java.net.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.client.*;
import net.minecraft.util.*;
import java.io.*;

public class ThreadVersionCheck extends Thread
{
    public static ThreadVersionCheck INSTANCE;
    private int count;
    public static int remoteMajVer;
    public static int remoteMinVer;
    public static int remoteBuildVer;
    
    public ThreadVersionCheck() {
        super("Galacticraft Version Check Thread");
        this.count = 0;
    }
    
    public static void startCheck() {
        final Thread thread = new Thread(ThreadVersionCheck.INSTANCE);
        thread.start();
    }
    
    @Override
    public void run() {
        final Side sideToCheck = FMLCommonHandler.instance().getSide();
        if (sideToCheck == null || ConfigManagerCore.disableUpdateCheck) {
            return;
        }
        while (this.count < 3 && ThreadVersionCheck.remoteBuildVer == 0) {
            BufferedReader in = null;
            try {
                final URL url = new URL("http://micdoodle8.com/galacticraft/version.html");
                final HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.addRequestProperty("User-Agent", "Mozilla/4.76");
                final InputStreamReader streamReader = new InputStreamReader(http.getInputStream());
                in = new BufferedReader(streamReader);
                String[] str2 = null;
                String str3;
                while ((str3 = in.readLine()) != null) {
                    if (str3.contains("Version")) {
                        str3 = str3.replace("Version=", "");
                        str2 = str3.split("#");
                        if (str2.length == 3) {
                            ThreadVersionCheck.remoteMajVer = Integer.parseInt(str2[0]);
                            ThreadVersionCheck.remoteMinVer = Integer.parseInt(str2[1]);
                            ThreadVersionCheck.remoteBuildVer = Integer.parseInt(str2[2]);
                        }
                        if (ThreadVersionCheck.remoteMajVer != 3 || (ThreadVersionCheck.remoteMinVer <= 0 && (ThreadVersionCheck.remoteMinVer != 0 || ThreadVersionCheck.remoteBuildVer <= 12))) {
                            continue;
                        }
                        Thread.sleep(5000L);
                        if (sideToCheck.equals((Object)Side.CLIENT)) {
                            FMLClientHandler.instance().getClient().thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumColor.GREY + "New " + EnumColor.DARK_AQUA + "Galacticraft" + EnumColor.GREY + " version available! v" + String.valueOf(ThreadVersionCheck.remoteMajVer) + "." + String.valueOf(ThreadVersionCheck.remoteMinVer) + "." + String.valueOf(ThreadVersionCheck.remoteBuildVer) + EnumColor.DARK_BLUE + " http://micdoodle8.com/"));
                        }
                        else {
                            if (!sideToCheck.equals((Object)Side.SERVER)) {
                                continue;
                            }
                            GCLog.severe("New Galacticraft version available! v" + String.valueOf(ThreadVersionCheck.remoteMajVer) + "." + String.valueOf(ThreadVersionCheck.remoteMinVer) + "." + String.valueOf(ThreadVersionCheck.remoteBuildVer) + " http://micdoodle8.com/");
                        }
                    }
                }
                in.close();
                streamReader.close();
            }
            catch (Exception e2) {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (ThreadVersionCheck.remoteBuildVer == 0) {
                try {
                    GCLog.severe(GCCoreUtil.translate("newversion.failed.name"));
                    Thread.sleep(15000L);
                }
                catch (InterruptedException ex) {}
            }
            else {
                GCLog.info(GCCoreUtil.translate("newversion.success.name") + " " + ThreadVersionCheck.remoteMajVer + "." + ThreadVersionCheck.remoteMinVer + "." + ThreadVersionCheck.remoteBuildVer);
            }
            ++this.count;
        }
    }
    
    static {
        ThreadVersionCheck.INSTANCE = new ThreadVersionCheck();
    }
}
