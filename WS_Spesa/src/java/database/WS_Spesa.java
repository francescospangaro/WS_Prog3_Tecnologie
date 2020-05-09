/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Galimberti Francesco
 */
public class WS_Spesa extends HttpServlet {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesa;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesa = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            spesa.close();
        } catch (SQLException e) {
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Spesa</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet WS_Spesa at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     *
     * //VISUALIZZAZIONE LISTA UTENTI examples of use:
     * http://localhost:8080/spesa/utenti
     * http://localhost:8080/spesa/utenti?username=fraGali
     * http://localhost:8080/spesa/utenti?nome=Francesco
     * http://localhost:8080/spesa/utenti?cognome=Caso
     * http://localhost:8080/spesa/utenti?regione=Lombardia
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String richiesta;
        String url;
        String[] url_section;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL

        url = request.getRequestURL().toString();
        url_section = url.split("/");
        richiesta = url_section[url_section.length - 1];

        System.out.println("richiesta " + richiesta);

        if (richiesta == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (richiesta.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (richiesta.equals("spesa")) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti";

            String username = request.getParameter("username");
            if (username != null) {
                sql += " WHERE username='" + username + "';";
            }
            String nome = request.getParameter("nome");
            if (nome != null) {
                sql += " WHERE nome='" + nome + "';";
            }
            String cognome = request.getParameter("cognome");
            if (cognome != null) {
                sql += " WHERE cognome='" + cognome + "';";
            }
            String regione = request.getParameter("regione");
            if (regione != null) {
                sql += " WHERE regione='" + regione + "';";
            }

            // ricerca nominativo nel database
            Statement statement = spesa.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Utente> utenti = new ArrayList<Utente>(0);
            while (result.next()) {
                String idUtente = result.getString("idUtente");
                String Username = result.getString("username");
                String Nome = result.getString("nome");
                String Cognome = result.getString("cognome");
                String CodiceFiscale = result.getString("codiceFiscale");
                String Regione = result.getString("regione");
                String Via = result.getString("via");
                String nCivico = result.getString("nCivico");
                Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
                utenti.add(u);
            }
            result.close();
            statement.close();

            // scrittura del body della risposta
            response.setContentType("text/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<elencoUtenti>");
                if (utenti.size() > 0) {
                    for (int i = 0; i < utenti.size(); i++) {
                        Utente u = utenti.get(i);
                        out.println("<Utente>");
                        out.println("<idUtente>" + u.getIdUtente() + "</idUtente>");
                        out.println("<username>" + u.getUsername() + "</username>");
                        out.println("<nome>" + u.getNome() + "</nome>");
                        out.println("<cognome>" + u.getCognome() + "</cognome>");
                        out.println("<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>");
                        out.println("<regione>" + u.getRegione() + "</regione>");
                        out.println("<via>" + u.getVia() + "</via>");
                        out.println("<nCivico>" + u.getnCivico() + "</nCivico>");
                        out.println("</Utente>");
                    }
                    utenti = new ArrayList<Utente>(0);
                }
                out.println("</elencoUtenti>");
            } finally {
                out.close();
            }
            response.setStatus(200); // OK

        } catch (SQLException e) {
            response.sendError(500, "DBMS server error!");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * INSERIMENTO RISPOSTA AD UNA RISCHIESTA DI SPESA in body risposta
     * POST spesa/risposte
     * 
     * <risposta>
     * <idUtente>1</idUtente>
     * <idRichiesta>2</idRichiesta>
     * </risposta>
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);
        String line;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        try {
            // scrittura nel file "risposta.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("risposta.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();
            // estrazione dei valori degli elementi "idUtente" e "idRichiesta" dal file "risposta.xml"
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("risposta.xml");
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
            if (idUtente == null || idRichiesta == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (idUtente.isEmpty() || idRichiesta.isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            try {
                // aggiunta voce nel database
                Statement statement = spesa.createStatement();
                if (statement.executeUpdate("INSERT risposte(rifUtente, rifRichiesta) VALUES(" + idUtente + ", " + idRichiesta + ");") <= 0) {
                    response.sendError(403, "Risposta exist!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(200); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * AGGIORNAMENTO DI UN UTENTE con idUtente nell'url e nel body xmlUtente
     *
     * PUT spesa/utenti/1
     *
     * body examples
     * <utente>
     * <idUtente>1</idUtente>
     * <username>fraGali</username>
     * <nome>Francesco</nome>
     * <cognome>Galimberti</cognome>
     * <codiceFiscale>GLMFNC01A02B729Q</codiceFiscale>
     * <regione>Lombardia</regione>
     * <via>Giacomo Leopardi</via>
     * <nCivico>5</nCivico>
     * </utente>
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url_idUtente;
        String url;
        String line;
        String[] url_section;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL
        url = request.getRequestURL().toString();
        url_section = url.split("/");
        url_idUtente = url_section[url_section.length - 1];
        if (url_idUtente == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_idUtente.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_idUtente.equals("spesa") || url_idUtente.equals("utenti")) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            // scrittura nel file "utente.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("utente.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();
            // estrazione dei valori dell'utente dal file "utente.xml"
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("utente.xml");

            Element root = document.getDocumentElement();
            Utente u = getUtente(root);
            
            if (u.getIdUtente() == null || u.getNome() == null || u.getCognome() == null || u.getCognome() == null || u.getCodiceFiscale() == null || u.getRegione() == null || u.getnCivico() == null || u.getVia() == null || u.getUsername() == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (u.getIdUtente().isEmpty() || u.getNome().isEmpty() || u.getCognome().isEmpty() || u.getCognome().isEmpty() || u.getCodiceFiscale().isEmpty() || u.getRegione().isEmpty() || u.getnCivico().isEmpty() || u.getVia().isEmpty() || u.getUsername().isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (!u.getIdUtente().equalsIgnoreCase(url_idUtente)) {
                response.sendError(400, "URL idUtente mismtach XML idUtente!");
                return;
            }
            try {
                Statement statement = spesa.createStatement();
                String sql="UPDATE utenti SET username='" + u.getUsername() + "', nome='" + u.getNome() + "', cognome='" + u.getCognome() + "', codiceFiscale='" + u.getCodiceFiscale() + "', regione='" + u.getRegione() + "', via='" + u.getVia() + "', nCivico='" + u.getnCivico() + "' WHERE idUtente = " + u.getIdUtente() + ";";
                if (statement.executeUpdate(sql) <= 0) {
                    response.sendError(404, "idUtente not found!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(200); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    private Utente getUtente(Element root) {
        String idUtente = getTextValue(root,"idUtente");
        String Username = getTextValue(root,"username");
        String Nome = getTextValue(root,"nome");
        String Cognome = getTextValue(root,"cognome");
        String CodiceFiscale = getTextValue(root,"codiceFiscale");
        String Regione = getTextValue(root,"regione");
        String Via = getTextValue(root,"via");
        String nCivico = getTextValue(root,"nCivico");
        Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
        return u;
    }
    
    private String getTextValue(Element element, String tag) {
        String value = null;
        NodeList nodelist;
        nodelist = element.getElementsByTagName(tag);
        if (nodelist != null && nodelist.getLength() > 0) {
            value = nodelist.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * ELIMINAZIONE DI UNA RICHIESTA DI SPESA con idRichiesta nell'url
     *
     * DELETE spesa/richieste/1
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url_idRichiesta;
        String url;
        String[] url_section;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL
        url = request.getRequestURL().toString();
        url_section = url.split("/");
        url_idRichiesta = url_section[url_section.length - 1];
        if (url_idRichiesta == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_idRichiesta.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_idRichiesta.equals("spesa") || url_idRichiesta.equals("richieste")) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            Statement statement = spesa.createStatement();
            String sql = "DELETE FROM richieste WHERE idRichiesta = " + url_idRichiesta + ";";
            if (statement.executeUpdate(sql) <= 0) {
                response.sendError(404, "idRichiesta not found!");
                statement.close();
                return;
            }
            statement.close();
            response.setStatus(200); // OK
        } catch (SQLException e) {
            response.sendError(500, "DBMS server error!");
            return;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SpesaDB";
    }// </editor-fold>

}
