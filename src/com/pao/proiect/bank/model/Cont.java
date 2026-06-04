package com.pao.proiect.bank.model;

import java.util.Objects;

public abstract class Cont {
    protected String iban;
    protected double sold;
    protected Client titular;

    public Cont(String iban, Client titular) {
        this.iban     = iban;
        this.titular  = titular;
        this.sold     = 0;
    }

    public abstract String getTipCont();

    public void depunere(double suma) { this.sold += suma; }

    public boolean retragere(double suma) {
        if (sold >= suma) { this.sold -= suma; return true; }
        return false;
    }

    public String getIban()    { return iban; }
    public double getSold()    { return sold; }
    public Client getTitular() { return titular; }

    public void setSold(double sold) { this.sold = sold; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cont)) return false;
        return Objects.equals(iban, ((Cont) o).iban);
    }

    @Override
    public int hashCode() { return Objects.hash(iban); }
}
