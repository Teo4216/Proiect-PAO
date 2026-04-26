package com.pao.proiect.bank.model;

public class Client extends Persoana {
    private String email;

    public Client(String nume, String cnp, String email) {
        super(nume, cnp);
        this.email = email;
    }

    @Override
    public String getRol() { return "CLIENT_BANCAR"; }

    @Override
    public String toString() {
        return "Client: " + nume + " (CNP: " + cnp + ")";
    }
}