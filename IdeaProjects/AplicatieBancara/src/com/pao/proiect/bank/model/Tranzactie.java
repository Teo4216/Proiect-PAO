package com.pao.proiect.bank.model;
import java.util.Date;

public final class Tranzactie {
    private final String id;
    private final double suma;
    private final String tip;
    private final Date data;

    public Tranzactie(String id, double suma, String tip) {
        this.id = id;
        this.suma = suma;
        this.tip = tip;
        this.data = new Date();
    }

    public String getId() { return id; }
    public double getSuma() { return suma; }
    public String getTip() { return tip; }
    public Date getData() { return new Date(data.getTime()); }

    @Override
    public String toString() {
        return "Tranzactie{" + "id='" + id + '\'' + ", suma=" + suma + ", tip='" + tip + '\'' + ", data=" + data + '}';
    }
}