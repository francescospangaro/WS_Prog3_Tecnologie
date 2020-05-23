/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author prof
 */
public class MyLibXML {
    
    // restituisce il valore testuale dell’elemento figlio specificato
    public static String getTextValue(Element element, String tag) {
        String value = null;
        NodeList nodelist;
        nodelist = element.getElementsByTagName(tag);
        if (nodelist != null && nodelist.getLength() > 0) {
            value = nodelist.item(0).getFirstChild().getNodeValue();
        }
        System.out.println("getTextValue(Element element," + " tag=" + tag + ") = " + value);
        return value;
    }
    
    // restituisce il valore testuale dell’elemento figlio specificato in posizione position nella lista dei figli
    public static String getTextValuePosItem(Element element, String tag, int position) {
        String value = "";
        if (element != null) {
            NodeList nodelist = element.getElementsByTagName(tag);

            if (nodelist != null && nodelist.getLength() > 0) {
                if ((nodelist.item(position) != null) && (nodelist.item(position).getChildNodes().getLength() > 0)) {
                    value = nodelist.item(position).getFirstChild().getNodeValue();
                }
            }
        }
        System.out.println("getTextValuePosItem(Element element," + " tag=" + tag + ", position=" + position + ") = " + value);  
        return value;
    }

    // restituisce il valore intero dell’elemento figlio specificato
    public static int getIntValue(Element element, String tag) {
        return Integer.parseInt(getTextValue(element, tag));
    }
    
    // restituisce il valore numerico dell’elemento figlio specificato
    public static float getFloatValue(Element element, String tag) {
        return Float.parseFloat(getTextValue(element, tag));
    }  
    
    public static String getAttribute(Element element, String attributeName) {
        return element.getAttribute(attributeName);
    }
    
    public static double getDoubleValue(Element element, String tag) {
        return Double.parseDouble(getTextValue(element,tag));
    }
}
