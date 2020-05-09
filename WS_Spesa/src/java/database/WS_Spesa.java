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
     * examples of use: 
     *    http://localhost:8080/spesa/utenti
     *    http://localhost:8080/spesa/utenti?username=fraGali
     *    http://localhost:8080/spesa/utenti?nome=Francesco
     *    http://localhost:8080/spesa/utenti?cognome=Caso
     *    http://localhost:8080/spesa/utenti?regione=Lombardia
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
            if (username != null){
                sql += " WHERE username='"+username+"';";
            }
            String nome = request.getParameter("nome");
            if (nome != null){
                sql += " WHERE nome='"+nome+"';";
            }
            String cognome = request.getParameter("cognome");
            if (cognome != null){
                sql += " WHERE cognome='"+cognome+"';";
            }
            String regione = request.getParameter("regione");
            if (regione != null){
                sql += " WHERE regione='"+regione+"';";
            }
            
            // ricerca nominativo nel database
            Statement statement = spesa.createStatement();
            ResultSet result = statement.executeQuery(sql);
            
            ArrayList<Utente> utenti = new ArrayList<Utente>(0);
            while(result.next()){
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
                if(utenti.size()>0){
                    for(int i=0;i<utenti.size();i++){
                        Utente u = utenti.get(i);
                        out.println("<Utente>");
                        out.println("<idUtente>"+ u.getIdUtente()+"</idUtente>");
                        out.println("<username>"+ u.getUsername()+"</username>");
                        out.println("<nome>"+ u.getNome()+"</nome>");
                        out.println("<cognome>"+ u.getCognome()+"</cognome>");
                        out.println("<codiceFiscale>"+ u.getCodiceFiscale()+"</codiceFiscale>");
                        out.println("<regione>"+ u.getRegione()+"</regione>");
                        out.println("<via>"+ u.getVia()+"</via>");
                        out.println("<nCivico>"+ u.getnCivico()+"</nCivico>");
                        out.println("</Utente>");
                    }
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
            // scrittura nel file "entry.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();
            // estrazione dei valori degli elementi "name" e "number" dal file "entry.xml"
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("entry.xml");
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("name");
            String name = null;
            if (list != null && list.getLength() > 0) {
                name = list.item(0).getFirstChild().getNodeValue();
            }
            list = root.getElementsByTagName("number");
            String number = null;
            if (list != null && list.getLength() > 0) {
                number = list.item(0).getFirstChild().getNodeValue();
            }
            if (name == null || number == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (name.isEmpty() || number.isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            try {
                // aggiunta voce nel database
                Statement statement = spesa.createStatement();
                if (statement.executeUpdate("INSERT Phonebook(Name, Number) VALUES('" + name + "', '" + number + "');") <= 0) {
                    response.sendError(403, "Name exist!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(201); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    // richiesta PUT
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url_name;
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
        url_name = url_section[url_section.length - 1];
        if (url_name == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_name.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            // scrittura nel file "entry.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();
            // estrazione dei valori degli elementi "name" e "number" dal file "entry.xml"
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("entry.xml");
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("name");
            String name = null;
            if (list != null && list.getLength() > 0) {
                name = list.item(0).getFirstChild().getNodeValue();
            }
            list = root.getElementsByTagName("number");
            String number = null;
            if (list != null && list.getLength() > 0) {
                number = list.item(0).getFirstChild().getNodeValue();
            }
            if (name == null || number == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (name.isEmpty() || number.isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (!name.equalsIgnoreCase(url_name)) {
                response.sendError(400, "URL name mismtach XML name!");
                return;
            }
            try {
                Statement statement = spesa.createStatement();
                if (statement.executeUpdate("UPDATE Phonebook SET Number='" + number + "'WHERE Name = '" + name + "';") <= 0) {
                    response.sendError(404, "Entry not found!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(204); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    // richiesta DELETE
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name;
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
        name = url_section[url_section.length - 1];
        if (name == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (name.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            Statement statement = spesa.createStatement();
            if (statement.executeUpdate("DELETE FROM Phonebook WHERE Name = '" + name + "';") <= 0) {
                response.sendError(404, "Entry not found!");
                statement.close();
                return;
            }
            statement.close();
            response.setStatus(204); // OK
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
