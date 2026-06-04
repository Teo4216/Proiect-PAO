package com.pao.proiect.bank.model;

public abstract class Persoana {
    protected String nume;
    protected String cnp;

    public Persoana(String nume, String cnp) {
        this.nume = nume;
        this.cnp  = cnp;
    }

    public abstract String getRol();
    public String getNume() { return nume; }
    public String getCnp()  { return cnp;  }
}
