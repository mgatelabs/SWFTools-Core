package com.mgatelabs.swftools.support.xml;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class XML_Reader {

    public Document getXML(InputStream is) {
        int tryCount = 0;

        try {
            Document doc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setIgnoringComments(true);
            factory.setCoalescing(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new CustomResolver());

            doc = builder.parse(is);
            return doc;
        }//end try
        catch (IOException ioe) {
            System.out.println("Error reading input.");
            System.out.println(ioe.getMessage());
        }//end catch
        catch (ParserConfigurationException pce) {
            System.out.println("Could not create that parser.");
            System.out.println(pce.getMessage());
        }//end catch
        catch (Exception ex) {
            System.out.println("Exception");
            System.out.println(ex.getMessage());
        }//end catch

        System.out.println("Error : Skipping XML");
        return null;
    }

    public Document getXML(File filename) {
        int tryCount = 0;

        if (!filename.exists()) {
            System.out.println("Error : " + filename + " does not exist.");
        }//end if
        else if (!filename.canRead()) {
            System.out.println("Error : " + filename + " not Readable.");
        }//end if else

        try {
            Document doc = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setIgnoringComments(true);
            factory.setCoalescing(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new CustomResolver());

            doc = builder.parse(new BufferedInputStream(new FileInputStream(filename), 2024));
            return doc;
        }//end try
        catch (IOException ioe) {
            System.out.println("Error reading input.");
            System.out.println(ioe.getMessage());
        }//end catch
        catch (ParserConfigurationException pce) {
            System.out.println("Could not create that parser.");
            System.out.println(pce.getMessage());
        }//end catch
        catch (Exception ex) {
            System.out.println("Exception");
            System.out.println(ex.getMessage());
        }//end catch

        System.out.println("Error : Skipping : " + filename.toString());
        return null;
    }//end getXML
}//edn XML_READER