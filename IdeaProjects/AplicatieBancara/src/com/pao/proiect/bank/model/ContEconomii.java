package com.pao.proiect.bank.model;

public class ContEconomii extends Cont {
    private double dobanda;

    public ContEconomii(String iban, Client titular, double dobanda) {
        super(iban, titular);
        this.dobanda = dobanda;
    }

    public void aplicaDobanda() {
        this.sold += (this.sold * dobanda / 100);
    }

    @Override
    public String getTipCont() {
        return "Economii";
    }

    @Override
    public String toString() {
        return "Cont Economii [IBAN=" + iban + ", sold=" + sold + ", dobanda=" + dobanda + "%]";
    }
}