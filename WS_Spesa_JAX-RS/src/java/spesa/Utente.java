/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

/**
 *
 * @author Galimberti Francesco
 */
public class Utente {
    private String idUtente;
    private String username;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String regione;
    private String via;
    private String nCivico;

    public Utente(String idUtente, String username, String nome, String cognome, String codiceFiscale, String regione, String via, String nCivico) {
        this.idUtente = idUtente;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.regione = regione;
        this.via = via;
        this.nCivico = nCivico;
    }

    public Utente() {
        this.idUtente = "";
        this.username = "";
        this.nome = "";
        this.cognome = "";
        this.codiceFiscale = "";
        this.regione = "";
        this.via = "";
        this.nCivico = "";
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getRegione() {
        return regione;
    }

    public String getVia() {
        return via;
    }

    public String getnCivico() {
        return nCivico;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public void setnCivico(String nCivico) {
        this.nCivico = nCivico;
    }
    
    
}
