package net.quetzi.whitelister.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.quetzi.whitelister.Whitelister;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistFetcher implements Runnable
{
    @Override
    public void run()
    {
        Thread.currentThread().setName("Whitelister");
        while (!Thread.currentThread().isInterrupted())
        {
            int reloadedLists = updateWhitelist();
            if (reloadedLists > 0)
            {
                Whitelister.log.info("Reloaded " + reloadedLists + "/" + Whitelister.whitelist.size() + " whitelist" + (reloadedLists > 1 ? "s." : "."));
            }
            else
            {
                Whitelister.log.info("Error reloading whitelist.");
            }
            try
            {
                Thread.currentThread().sleep(Whitelister.checkInterval * 60000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean writeWhitelist()
    {
        File whitelistSave;
        try
        {
            int listCount = 0;

            for (String url : Whitelister.whitelist.keySet())
            {
                listCount++;
                whitelistSave = new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName(), "../whitelist-" + listCount + ".txt");
                if (whitelistSave.exists())
                {
                    Whitelister.log.info(whitelistSave.delete() ? "Deleted whitelist " + whitelistSave.getName() : "Could not delete whitelist " + whitelistSave.getName());
                }
                if (!whitelistSave.createNewFile())
                {
                    Whitelister.log.info("Error saving whitelist");
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(whitelistSave));

                for (String player : Whitelister.whitelist.get(url))
                {
                    out.write(player + "\n");
                }
                out.close();
            }
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeJsonWhitelist()
    {
        File whitelistSave;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            int listCount = 0;
            for (String url : Whitelister.whitelist.keySet())
            {
                listCount++;
                whitelistSave = new File(FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName(), "../whitelist-" + listCount + ".json");
                if (whitelistSave.exists())
                {
                    Whitelister.log.info(whitelistSave.delete() ? "Deleted whitelist " + whitelistSave.getName() : "Could not delete whitelist " + whitelistSave.getName());
                }
                if (!whitelistSave.createNewFile())
                {
                    Whitelister.log.info("Error saving whitelist");
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(whitelistSave));
                out.write(gson.toJson(Whitelister.whitelist.get(url)));
                out.close();
            }
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static int updateWhitelist()
    {
        HashMap<String, Set<String>> cachedWhitelist = Whitelister.whitelist;
        int                          successCount    = 0;
        Whitelister.whitelist = new HashMap<>();
        if (!Arrays.equals(Whitelister.defaultUrls, Whitelister.urlList))
        {
            for (String url : Whitelister.urlList)
            {
                if (processList(url, false))
                {
                    successCount++;
                }
                else
                {
                    Whitelister.log.warn("Failed to fetch whitelist from " + url + " using cached list for this source");
                    Whitelister.whitelist.put(url, cachedWhitelist.get(url));
                }
            }
            // TODO: Make this code only run once (or just send difference)
//            // Add to Headcrumbs
//            if (Loader.isModLoaded("headcrumbs") && Whitelister.headcrumbsCompat)
//            {
//                for (Set<String> strings : Whitelister.whitelist.values())
//                {
//                    for (String s : strings)
//                    {
//                        FMLInterModComms.sendMessage("headcrumbs", "add-username", s);
//                    }
//                }
//            }
        }
        if (!Arrays.equals(Whitelister.defaultJsonUrls, Whitelister.jsonList))
        {
            for (String url : Whitelister.jsonList)
            {
                if (processList(url, true))
                {
                    successCount++;
                }
                else
                {
                    Whitelister.log.warn("Failed to fetch whitelist from " + url + " using cached list for this source");
                    Whitelister.whitelist.put(url, cachedWhitelist.get(url));
                }
            }
            // TODO: Make this code only run once (or just send difference)
//            // Add to Headcrumbs
//            if (Loader.isModLoaded("headcrumbs") && Whitelister.headcrumbsCompat)
//            {
//                for (Set<String> strings : Whitelister.whitelist.values())
//                {
//                    for (String s : strings)
//                    {
//                        FMLInterModComms.sendMessage("headcrumbs", "add-username", s);
//                    }
//                }
//            }
        }
        return successCount;
    }

    private static boolean processList(String url, boolean isJson)
    {
        if (isJson)
        {
            return getRemoteJsonWhitelist(url);
        }
        else
        {
            return getRemoteWhitelist(url);
        }
    }

    private static boolean getRemoteWhitelist(String urlString)
    {
        try
        {
            URL               url  = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                Set<String> tempList = new HashSet<>();
                String      inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    inputLine = inputLine.trim();
                    tempList.add(inputLine.toLowerCase());
                }
                if (!tempList.isEmpty())
                {
                    if (!tempList.contains("cannot connect to localhost as bashtech:"))
                    {
                        Whitelister.whitelist.put(urlString, tempList);
                    }
                    else
                    {
                        return false;
                    }
                }
                in.close();
            }
            catch (IOException e)
            {
                String      errorIn     = "";
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null)
                {
                    BufferedReader inE = new BufferedReader(new InputStreamReader(errorStream));
                    String         inputLine;
                    while ((inputLine = inE.readLine()) != null)
                    {
                        errorIn = errorIn + inputLine;
                    }
                    inE.close();
                }
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    private class Users
    {
        String whitelist_name;
        int    active;
        int    manual;
    }

    private static boolean getRemoteJsonWhitelist(String urlString)
    {
        try
        {
            URL               url  = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try
            {
                BufferedReader in        = new BufferedReader((new InputStreamReader(conn.getInputStream())));
                JsonReader     r         = new JsonReader(in);
                Set<Users>     jsonInput = new HashSet<>();
                Gson           gson      = new Gson();
                jsonInput = gson.fromJson(in, jsonInput.getClass());
                if (jsonInput.isEmpty())
                {
                    return false;
                }
                else
                {
                    Set<String> tempList = new HashSet<>();
                    for (Users user : jsonInput)
                    {
                        tempList.add(user.whitelist_name);
                    }
                    Whitelister.whitelist.put(urlString, tempList);
                }
            }
            catch (IOException ex)
            {
                Whitelister.log.error(ex.getMessage());
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            Whitelister.log.error(ex.getMessage());
        }
        return false;
    }
}
