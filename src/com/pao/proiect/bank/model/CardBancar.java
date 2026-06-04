package com.pao.proiect.bank.model;

public class CardBancar {
    private String numarCard;
    private Cont   contAsociat;

    public CardBancar(String numarCard, Cont contAsociat) {
        this.numarCard    = numarCard;
        this.contAsociat  = contAsociat;
    }

    public String getNumarCard()  { return numarCard; }
    public Cont   getContAsociat() { return contAsociat; }

    @Override
    public String toString() {
        return "Card: " + numarCard + " -> " + contAsociat.getIban();
    }
}
