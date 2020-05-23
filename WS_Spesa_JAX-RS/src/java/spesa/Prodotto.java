/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

/**
 *
 * @author Luca
 */
public class Prodotto {
    private int idProdotto;
    private String genere;
    private String etichetta;
    private String nome;
    private String marca;
    private double costo;
    private String descrizione;

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getEtichetta() {
        return etichetta;
    }

    public void setEtichetta(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Prodotto(int idProdotto, String genere, String etichetta, double costo, String nome, String marca, String descrizione) {
        this.idProdotto = idProdotto;
        this.genere = genere;
        this.etichetta = etichetta;
        this.nome = nome;
        this.marca = marca;
        this.costo = costo;
        this.descrizione = descrizione;
    }

    public Prodotto(String genere, String etichetta, double costo, String nome, String marca, String descrizione) {
        this.genere = genere;
        this.etichetta = etichetta;
        this.nome = nome;
        this.marca = marca;
        this.costo = costo;
        this.descrizione = descrizione;
    }
    
    
}
