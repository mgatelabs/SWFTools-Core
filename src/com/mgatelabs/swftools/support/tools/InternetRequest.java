package com.mgatelabs.swftools.support.tools;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

public class InternetRequest {

    public static String request(String link) {
        String result = "", temp = "";
        try {
            URL myDestination = new URL(link);

            InputStream is = myDestination.openStream();
            DataInputStream dis = new DataInputStream(is);

            while ((temp = dis.readLine()) != null) {
                result += temp;
            }

            dis.close();
            return result;
        } catch (Exception ex) {
            System.out.println("Login: " + ex.getMessage());
            return "";
        }
    }

}