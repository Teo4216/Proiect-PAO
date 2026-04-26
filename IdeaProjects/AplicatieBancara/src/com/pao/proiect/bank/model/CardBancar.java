package com.pao.proiect.bank.model;

public class CardBancar {
    private String numarCard;
    private Cont contAsociat;

    public CardBancar(String numarCard, Cont contAsociat) {
        this.numarCard = numarCard;
        this.contAsociat = contAsociat;
    }

    public String getNumarCard() { return numarCard; }

    @Override
    public String toString() {
        return "Card: " + numarCard + " -> " + contAsociat.getIban();
    }
}