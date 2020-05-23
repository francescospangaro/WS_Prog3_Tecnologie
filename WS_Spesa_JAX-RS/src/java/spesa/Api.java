/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
/**
 * REST Web Service
 *
 * @author Galimberti Francesco
 */
@ApplicationPath("")
@Path("")
public class Api extends Application{

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
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

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Api
     */
    public Api() {
        super();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("prova")
    public Response getMessage() {
        Response r = Response.ok("test with GET")
                             .build();
        return r;
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
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("utenti")
    public Response getUtenti(@PathParam(value = "utenti") String utenti,
            @DefaultValue("null") @QueryParam("username") String username,
            @DefaultValue("null") @QueryParam("nome") String nome,
            @DefaultValue("null") @QueryParam("cognome") String cognome,
            @DefaultValue("null") @QueryParam("regione") String regione) {

        init();
        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().build();
            return r;

        } else {

            try {
                String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti";

                if (!username.equalsIgnoreCase("null")) {
                    sql += " WHERE username='" + username + "';";
                }

                if (!nome.equalsIgnoreCase("null")) {
                    sql += " WHERE nome='" + nome + "';";
                }

                if (!cognome.equalsIgnoreCase("null")) {
                    sql += " WHERE cognome='" + cognome + "';";
                }

                if (!regione.equalsIgnoreCase("null")) {
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
                destroy();
                
                r = Response.ok(output).build();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().build();
                return r;
            }   
            
        }
    }

    /**
     * AGGIORNAMENTO DI UN UTENTE con idUtente nell'url e nel body xmlUtente
     *
     * PUT spesa/utenti/1
     *
     * body examples
      <utente>
      <idUtente>1</idUtente>
      <username>fraGali</username>
      <nome>Francesco</nome>
      <cognome>Galimberti</cognome>
      <codiceFiscale>GLMFNC01A02B729Q</codiceFiscale>
      <regione>Lombardia</regione>
      <via>Giacomo Leopardi</via>
      <nCivico>5</nCivico>
      </utente>
     */
    @PUT
    @Path("utenti/{idUtente}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_XML})
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
    @Path("putRisposta")
    public String putRisposta(String content) {

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
    
    
    /**
     * TOSETTI LUCA
     * 
     * http://localhost:8080/spesa/getRisposte
     * http://localhost:8080/spesa/getProdotto
     * http://localhost:8080/spesa/postProdotto
     * http://localhost:8080/spesa/putProdotto
     * http://localhost:8080/spesa/deleteProdotto
     */
    
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("getRisposte")
    public String getRisposte(@QueryParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        
        try {
            String sql = "SELECT idRisposta,rifRichiesta FROM risposte WHERE";
            if (id != null && id.isEmpty()) {
                sql = sql + " rifUtente='" + id + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);
            
            ArrayList<Risposta> risp = new ArrayList<Risposta>();
            while (result.next()) {
                String rispID = result.getString(1);
                String rispRifRichiesta = result.getString(2);

                risp.add(new Risposta(id, rispRifRichiesta,rispID));

            }
            
            if (risp.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < risp.size(); i++) {
                    output = output + "<risposta>\n";
                    output = output + "<idRisposta>" + risp.get(i).getIdRisposta()+ "</idRisposta>\n";
                    output = output + "<idUtente>" + risp.get(i).getIdUtente() + "</idUtente>\n";
                    output = output + "<idRichiesta>" + risp.get(i).getIdRichiesta()+ "</idRichiesta>\n";
                    output = output + "</risposta>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }
    
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("getProdotto")
    public String getProdotto(@QueryParam("genere") String genere, @QueryParam("etichetta") String etichetta, @QueryParam("costo") double costo, @QueryParam("nome") String nome, @QueryParam("marca") String marca, @QueryParam("descrizione") String descrizione) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT * FROM prodotti WHERE";
            if (genere != null) {
                sql = sql + " genere='" + genere + "' AND";
            }
            if (etichetta != null) {
                sql = sql + " etichetta='" + etichetta + "' AND";
            }
            if (costo != 0.00) {
                sql = sql + " costo='" + costo + "' AND";
            }
            if (nome != null) {
                sql = sql + " nome='" + nome + "' AND";
            }
            if (marca != null) {
                sql = sql + " marca='" + marca + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> prd = new ArrayList<Prodotto>();
            while (result.next()) {
                int prdID = result.getInt(1);
                String prdGenere = result.getString(2);
                String prdEtichetta = result.getString(3);
                double prdCosto = result.getDouble(4);
                String prdNome = result.getString(5);
                String prdMarca = result.getString(6);
                String prdDescrizione = result.getString(7);

                prd.add(new Prodotto(prdID, prdGenere, prdEtichetta, prdCosto, prdNome, prdMarca, prdDescrizione));

            }

            if (prd.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < prd.size(); i++) {
                    output = output + "<prodotto>\n";
                    output = output + "<idProdotto>" + prd.get(i).getIdProdotto() + "</idProdotto>\n";
                    output = output + "<genere>" + prd.get(i).getGenere() + "</genere>\n";
                    output = output + "<etichetta>" + prd.get(i).getEtichetta() + "</etichetta>\n";
                    output = output + "<costo>" + prd.get(i).getCosto() + "</costo>\n";
                    output = output + "<nome>" + prd.get(i).getNome() + "</nome>\n";
                    output = output + "<marca>" + prd.get(i).getMarca() + "</marca>\n";
                    output = output + "<descrizione>" + prd.get(i).getDescrizione() + "</descrizione>\n";
                    output = output + "</prodotto>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("postProdotto")
    public String postProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotti = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO prodotti (genere,etichetta,costo,nome,marca,descrizione) VALUES ('" + prodotti.get(0).getGenere() + "','" + prodotti.get(0).getEtichetta() + "','" + prodotti.get(0).getCosto() + "','" + prodotti.get(0).getNome() + "','" + prodotti.get(0).getMarca() + "','" + prodotti.get(0).getDescrizione() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("putProdotto")
    public String putProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotto = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            
            if(prodotto.get(0).getIdProdotto()==0 || prodotto.get(0).getGenere()==null || prodotto.get(0).getEtichetta()==null || prodotto.get(0).getNome()==null || prodotto.get(0).getMarca()==null || prodotto.get(0).getCosto()==0.00 || prodotto.get(0).getDescrizione()==null) {
                return "<errorMessage>400</errorMessage>";
            }
            if(prodotto.get(0).getGenere().isEmpty() || prodotto.get(0).getEtichetta().isEmpty() || prodotto.get(0).getNome().isEmpty() || prodotto.get(0).getMarca().isEmpty() || prodotto.get(0).getDescrizione().isEmpty()) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE prodotti SET nome='" + prodotto.get(0).getNome() + "', genere='" + prodotto.get(0).getGenere() + "', etichetta='" + prodotto.get(0).getEtichetta() + "', costo='" + prodotto.get(0).getCosto() + "', nome='" + prodotto.get(0).getNome() + "', marca='" + prodotto.get(0).getMarca() + "', descrizione='" + prodotto.get(0).getDescrizione() + "' WHERE idProdotto='" + prodotto.get(0).getIdProdotto() + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    @DELETE
    @Path("deleteProdotto")
    public String deleteProdotto(@QueryParam("id") int id) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        if (id != 0) {
            try {
                String sql = "DELETE FROM prodotti WHERE idProdotto='" + id + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } else {
            return "<errorMessage>406</errorMessage>";
        }

    }

}
