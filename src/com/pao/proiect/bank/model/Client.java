package com.pao.proiect.bank.model;

import java.util.Objects;

public class Client extends Persoana {
    private String email;

    public Client(String nume, String cnp, String email) {
        super(nume, cnp);
        this.email = email;
    }

    @Override
    public String getRol() { return "CLIENT_BANCAR"; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        return Objects.equals(cnp, ((Client) o).cnp);
    }

    @Override
    public int hashCode() { return Objects.hash(cnp); }

    @Override
    public String toString() {
        return "Client: " + nume + " (CNP: " + cnp + ", Email: " + email + ")";
    }
}
