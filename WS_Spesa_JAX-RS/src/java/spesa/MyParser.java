package Spesa;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.*;
import spesa.Utente;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class MyParser {

    public MyParser() {
    }

    public Utente parseFileUtente(String filename) throws ParserConfigurationException, SAXException, IOException {

        // estrazione dei valori dell'utente dal file "utente.xml"
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);

        Element root = document.getDocumentElement();
        Utente u = getUtente(root);

        return u;
    }

    public Risposta parseFileRisposta(String filename) throws ParserConfigurationException, SAXException, IOException {

        
        // estrazione dei valori degli elementi "idUtente" e "idRichiesta" dal file "risposta.xml"
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);
        Element root = document.getDocumentElement();

        NodeList list = root.getElementsByTagName("idUtente");
        String idUtente = null;
        if (list != null && list.getLength() > 0) {
            idUtente = list.item(0).getFirstChild().getNodeValue();
        }

        list = root.getElementsByTagName("idRichiesta");
        String idRichiesta = null;
        if (list != null && list.getLength() > 0) {
            idRichiesta = list.item(0).getFirstChild().getNodeValue();
        }
        
        Risposta r = new Risposta(idUtente,idRichiesta);

        return r;
    }

    /*public Richiesta parseRichiesta(String filename) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Richiesta richiesta;

        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);
        root = document.getDocumentElement();
        nodelist = root.getElementsByTagName("richiesta");
        element = (Element) nodelist.item(0);
        element.getNextSibling();
        richiesta = getRichiesta(element);
        richiestat = richiesta;

        return richiestat;
    }*/
    private Utente getUtente(Element root) {
        String idUtente = getTextValue(root, "idUtente");
        String Username = getTextValue(root, "username");
        String Nome = getTextValue(root, "nome");
        String Cognome = getTextValue(root, "cognome");
        String CodiceFiscale = getTextValue(root, "codiceFiscale");
        String Regione = getTextValue(root, "regione");
        String Via = getTextValue(root, "via");
        String nCivico = getTextValue(root, "nCivico");
        Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
        return u;
    }

    private Richiesta getRichiesta(Element elementUtente) {
        Richiesta richiesta;
        richiesta = new Richiesta();

        int rifUtente = getIntValue(elementUtente, "rifUtente");
        richiesta.setRifUtente(rifUtente);

        String oraInizio = getTextValue(elementUtente, "oraInizio");
        richiesta.setOraInizio(oraInizio);

        String oraFine = getTextValue(elementUtente, "oraFine");
        richiesta.setOraFine(oraFine);

        String durata = getTextValue(elementUtente, "durata");
        richiesta.setDurata(durata);

        return richiesta;
    }

    // restituisce il valore testuale dell’elemento figlio specificato
    private String getTextValue(Element element, String tag) {
        String value = null;
        NodeList nodelist;
        nodelist = element.getElementsByTagName(tag);
        if (nodelist != null && nodelist.getLength() > 0) {
            try {
                value = nodelist.item(0).getFirstChild().getNodeValue();
            } catch (Exception ex) {
                try {
                    value = nodelist.item(0).getNextSibling().getNodeValue();
                } catch (Exception e) {

                }
            }
            if (value == null) {
                value = "null";
            }
        }

        return value;
    }

    // restituisce il valore intero dell’elemento figlio specificato
    private int getIntValue(Element element, String tag) {
        return Integer.parseInt(getTextValue(element, tag));
    }

    // restituisce il valore numerico dell’elemento figlio specificato
    private float getFloatValue(Element element, String tag) {
        return Float.parseFloat(getTextValue(element, tag));
    }

    //restituisce il valore temporale dell'elemento figlio specificato
    private java.sql.Time getTimeValue(Element element, String tag) {
        String stringa = getTextValue(element, tag);
        DateFormat formato = new SimpleDateFormat("HH:mm:ss");
        java.sql.Time ora = null;
        try {
            ora = new java.sql.Time(formato.parse(stringa).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(MyParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ora;
    }

}
