package com.pao.proiect.bank.model;

public class ContCurent extends Cont {
    public ContCurent(String iban, Client titular) {
        super(iban, titular);
    }

    @Override
    public String getTipCont() {
        return "Curent";
    }

    @Override
    public String toString() {
        return "Cont Curent [IBAN=" + iban + ", sold=" + sold + "]";
    }
}