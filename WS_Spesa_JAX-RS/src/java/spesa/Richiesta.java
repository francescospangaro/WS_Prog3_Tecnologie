/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Spesa;

import java.sql.Time;

/**
 *
 * @author franc_000
 */
public class Richiesta {
    private int rifUtente;
    private String oraInizio, oraFine, durata;

    public Richiesta(int rifUtente, String oraInizio, String oraFine, String durata) {
        this.rifUtente = rifUtente;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.durata = durata;
    }

    public Richiesta() {
    }

    public int getRifUtente() {
        return rifUtente;
    }

    public void setRifUtente(int rifUtente) {
        this.rifUtente = rifUtente;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public String getOraFine() {
        return oraFine;
    }

    public void setOraFine(String oraFine) {
        this.oraFine = oraFine;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }
    
    
    
}
