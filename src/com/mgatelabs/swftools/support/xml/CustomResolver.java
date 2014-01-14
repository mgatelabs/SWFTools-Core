package com.mgatelabs.swftools.support.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class CustomResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId) {
        if (systemId.equals("http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd")) {
            return new InputSource("special/svg10.dtd");
        } else if (systemId.equals("http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd")) {
            return new InputSource("special/svg11.dtd");
        }
        return null;
    }
}