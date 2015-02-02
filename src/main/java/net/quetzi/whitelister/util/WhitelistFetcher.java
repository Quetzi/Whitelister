package net.quetzi.whitelister.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.quetzi.whitelister.Whitelister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistFetcher implements Runnable {

    @Override
    public void run() {

        Thread.currentThread().setName("Whitelister");
        while (!Thread.currentThread().isInterrupted()) {
            int reloadedLists = updateWhitelist();
            if (reloadedLists > 0) {
                Whitelister.log.info("Reloaded " + reloadedLists + "/" + Whitelister.whitelist.size() + " whitelist" + (reloadedLists > 1 ? "s." : "."));
            }
            else {
                Whitelister.log.info("Error reloading whitelist.");
            }
            try {
                Thread.currentThread().sleep(Whitelister.checkInterval * 60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean writeWhitelist() {

        File whitelistSave = new File(MinecraftServer.getServer().getDataDirectory(), "whitelist-export.txt");

        Whitelister.log.info("Whitelist export location: " + whitelistSave.getAbsolutePath());
        if (whitelistSave.exists()) whitelistSave.delete();
        try {
            if (!whitelistSave.createNewFile()) {
                Whitelister.log.info("Error saving whitelist: " + whitelistSave.getAbsolutePath());
            }
            FileWriter fstream = new FileWriter(whitelistSave);
            BufferedWriter out = new BufferedWriter(fstream);

            for (String url : Whitelister.whitelist.keySet()) {
                for (String player : Whitelister.whitelist.get(url)) {
                    out.write(player + "\n");
                }
            }
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int updateWhitelist() {

        HashMap<String, Set<String>> cachedWhitelist = Whitelister.whitelist;
        int successCount = 0;
        Whitelister.whitelist = new HashMap<String, Set<String>>();
        for(String url : Whitelister.urlList) {
            if (getRemoteWhitelist(url)) {
                successCount++;
            } else {
                Whitelister.log.warn("Failed to fetch whitelist from " + url + " using cached list for this source");
                Whitelister.whitelist.put(url, cachedWhitelist.get(url));
            }
        }
        return successCount;
    }

    private static boolean getRemoteWhitelist(String urlString) {

        try {

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                Set<String> tempList = new HashSet<String>();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    inputLine = inputLine.trim();
                    tempList.add(inputLine.toLowerCase());
                }
                if (!tempList.isEmpty()) {
                    Whitelister.whitelist.put(urlString, tempList);
                }
                in.close();
            } catch (IOException e) {
                String errorIn = "";
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    BufferedReader inE = new BufferedReader(new InputStreamReader(errorStream));
                    String inputLine;
                    while ((inputLine = inE.readLine()) != null)
                        errorIn = errorIn + inputLine;
                    inE.close();
                }
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
