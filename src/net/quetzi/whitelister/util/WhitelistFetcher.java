package net.quetzi.whitelister.util;

import net.minecraft.server.MinecraftServer;
import net.quetzi.whitelister.Whitelister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistFetcher implements Runnable {

    public void run() {

        Thread.currentThread().setName("Whitelister");
        while (!Thread.currentThread().isInterrupted()) {
            Whitelister.log.info("Reloading whitelist.");
            if (updateWhitelist()) {
                Whitelister.log.info("Whitelist reloaded.");
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

        File whitelistSave = new File(MinecraftServer.getServer().getFolderName(), "whitelist-export.txt");

        if (whitelistSave.exists()) whitelistSave.delete();
        try {
            if (!whitelistSave.createNewFile()) {
                Whitelister.log.info(("Error saving whitelist"));
            }
            FileWriter fstream = new FileWriter(whitelistSave);
            BufferedWriter out = new BufferedWriter(fstream);

            for (String player : Whitelister.whitelist) {
                out.write(player + "\n");
            }
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateWhitelist() {

        Set<String> cache = Whitelister.whitelist;
        Whitelister.whitelist.clear();
        for(String url : Whitelister.urlList) {
            if (getRemoteWhitelist(url)) {
                Whitelister.log.info("Fetched whitelist from " + url);
            } else {
                Whitelister.log.warn("Failed to fetch whitelist from " + url);
                Whitelister.log.warn("One or more whitelists failed to load, using cached whitelist");
                Whitelister.whitelist = cache;
                return false;
            }
        }
        return false;
    }

    private static boolean getRemoteWhitelist(String urlString) {

        try {

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    inputLine = inputLine.trim();
                    Whitelister.whitelist.add(inputLine.toLowerCase());
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
