/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Galimberti Francesco
 */
@ApplicationPath("/")
@Path("spesa")
public class Api {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Calcolatrice
     */
    public Api() {
    }

    // attivazione (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesaDatabase = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione (disconnessione da DBMS)
    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    /*
     * http://localhost:8080/spesa/utenti
     * http://localhost:8080/spesa/utenti?username=fraGali
     * http://localhost:8080/spesa/utenti?nome=Francesco
     * http://localhost:8080/spesa/utenti?cognome=Caso
     * http://localhost:8080/spesa/utenti?regione=Lombardia
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("utenti")
    public String getUtenti(@PathParam(value = "utenti") String utenti,
            @QueryParam("username") String username,
            @QueryParam("nome") String nome,
            @QueryParam("cognome") String cognome,
            @QueryParam("regione") String regione) {

        init();
        String output = "";

        // verifica stato connessione a DBMS
        if (!connected) {

            return "<errorMessage>500</errorMessage>";

        } else {

            try {
                String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti";

                if (username != null) {
                    sql += " WHERE username='" + username + "';";
                }

                if (nome != null) {
                    sql += " WHERE nome='" + nome + "';";
                }

                if (cognome != null) {
                    sql += " WHERE cognome='" + cognome + "';";
                }

                if (regione != null) {
                    sql += " WHERE regione='" + regione + "';";
                }

                // ricerca nominativo nel database
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                ArrayList<Utente> utentiList = new ArrayList<Utente>(0);
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
                    utentiList.add(u);
                }
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                output += "<elencoUtenti>";
                if (utentiList.size() > 0) {
                    for (int i = 0; i < utentiList.size(); i++) {
                        Utente u = utentiList.get(i);
                        output += "<Utente>";
                        output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                        output += "<username>" + u.getUsername() + "</username>";
                        output += "<nome>" + u.getNome() + "</nome>";
                        output += "<cognome>" + u.getCognome() + "</cognome>";
                        output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                        output += "<regione>" + u.getRegione() + "</regione>";
                        output += "<via>" + u.getVia() + "</via>";
                        output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                        output += "</Utente>";
                    }
                    utentiList = new ArrayList<Utente>(0);
                }
                output += "</elencoUtenti>";

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
            destroy();
            return output;
        }
    }

    /**
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
     * @param idUtente
     * @param content
     * @return
     */
    @PUT
    @Path("utenti/{idUtente}")
    @Consumes(MediaType.TEXT_XML)
    protected String putUtente(@PathParam("idUtente") String idUtente,
            String content) {
        // verifica stato connessione a DBMS
        init();
        MyParser myParse;

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {

                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("utente.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Utente u = myParse.parseFileUtente("utente.xml");

                if (u.getIdUtente() == null || u.getNome() == null || u.getCognome() == null || u.getCognome() == null || u.getCodiceFiscale() == null || u.getRegione() == null || u.getnCivico() == null || u.getVia() == null || u.getUsername() == null) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (u.getIdUtente().isEmpty() || u.getNome().isEmpty() || u.getCognome().isEmpty() || u.getCognome().isEmpty() || u.getCodiceFiscale().isEmpty() || u.getRegione().isEmpty() || u.getnCivico().isEmpty() || u.getVia().isEmpty() || u.getUsername().isEmpty()) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (!u.getIdUtente().equalsIgnoreCase(idUtente)) {
                    return "<errorMessage>400</errorMessage>";
                }

                Statement statement = spesaDatabase.createStatement();
                String sql = "UPDATE utenti SET username='" + u.getUsername() + "', nome='" + u.getNome() + "', cognome='" + u.getCognome() + "', codiceFiscale='" + u.getCodiceFiscale() + "', regione='" + u.getRegione() + "', via='" + u.getVia() + "', nCivico='" + u.getnCivico() + "' WHERE idUtente = " + u.getIdUtente() + ";";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>404</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";

            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        }
    }

    /**
     * ELIMINAZIONE DI UNA RICHIESTA DI SPESA con idRichiesta nell'url
     *
     * DELETE spesa/richieste/1
     */
    @DELETE
    @Path("richieste/{idRichiesta}")
    @Consumes(MediaType.TEXT_XML)
    protected String doDelete(@PathParam("idRichiesta") String idRichiesta) {

        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            if (idRichiesta == null) {
                return "<errorMessage>400</errorMessage>";
            }
            if (idRichiesta.isEmpty()) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                Statement statement = spesaDatabase.createStatement();
                String sql = "DELETE FROM richieste WHERE idRichiesta = " + idRichiesta + ";";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * INSERIMENTO RISPOSTA AD UNA RISCHIESTA DI SPESA in body risposta POST
     * spesa/risposte
     *
     * <risposta>
     * <idUtente>1</idUtente>
     * <idRichiesta>2</idRichiesta>
     * </risposta>
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("postProdotto")
    public String postProdotto(String content) {

        // verifica stato connessione a DBMS
        init();
        MyParser myParse;

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {

            try {
                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("risposta.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Risposta r = myParse.parseFileRisposta("risposta.xml");

                if (r.getIdUtente() == null || r.getIdRichiesta() == null) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (r.getIdUtente().isEmpty() || r.getIdRichiesta().isEmpty()) {
                    return "<errorMessage>400</errorMessage>";
                }

                try {
                    // aggiunta voce nel database
                    Statement statement = spesaDatabase.createStatement();
                    String sql ="INSERT risposte(rifUtente, rifRichiesta) VALUES(" + r.getIdUtente() + ", " + r.getIdRichiesta() + ");";
                    
                    if (statement.executeUpdate(sql) <= 0) {                        
                        statement.close();
                        return "<errorMessage>403</errorMessage>";
                    }
                    
                    statement.close();
                    destroy();
                    return "<message>Update avvenuto correttamente</message>";
                } catch (SQLException ex) {
                    Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    return "<errorMessage>500</errorMessage>";
                }
            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            }

        }        
    }

}
