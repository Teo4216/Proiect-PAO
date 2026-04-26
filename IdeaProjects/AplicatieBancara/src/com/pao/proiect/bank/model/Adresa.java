package com.pao.proiect.bank.model;

public class Adresa {
    private String oras;
    private String strada;

    public Adresa(String oras, String strada) {
        this.oras = oras;
        this.strada = strada;
    }

    @Override
    public String toString() {
        return oras + ", " + strada;
    }
}