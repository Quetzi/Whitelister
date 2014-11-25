package net.quetzi.whitelister.util;

import net.quetzi.whitelister.Whitelister;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Quetzi on 25/11/14.
 */
public class StringFetcher {

    public static String[] getStrings(String configString) {

        ArrayList<String> stringArray = new ArrayList<String>();
            try {
                BufferedReader in = new BufferedReader(new FileReader(configString));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    inputLine = inputLine.trim();
                    stringArray.add(inputLine.toLowerCase());
                }
                in.close();
            } catch (IOException e) {
                Whitelister.log.error("Failed to read file at " + configString);
            }
        String[] returnList = new String[stringArray.size()];
        Iterator ite = stringArray.iterator();
        int i = 0;
        while (ite.hasNext()) {
            returnList[i] = ite.next().toString();
            i++;
        }
        return returnList;
    }
}
