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
public class Risposta {
    private String idUtente;
    private String idRichiesta;
    private String idRisposta;
    
    public Risposta(String idUtente, String idRichiesta, String idRisposta) {
        this.idUtente = idUtente;
        this.idRichiesta = idRichiesta;
        this.idRisposta = idRisposta;
    }
    
    public Risposta() {
        this.idUtente = "";
        this.idRichiesta = "";
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getIdRichiesta() {
        return idRichiesta;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public void setIdRichiesta(String idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public String getIdRisposta() {
        return idRisposta;
    }

    public void setIdRisposta(String idRisposta) {
        this.idRisposta = idRisposta;
    }
    
    
    
}
