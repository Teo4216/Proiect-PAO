package com.pao.proiect.bank.service;

import com.pao.proiect.bank.exception.FonduriInsuficienteException;
import com.pao.proiect.bank.model.Client;
import com.pao.proiect.bank.model.Cont;
import com.pao.proiect.bank.model.Tranzactie;

import java.util.*;

public class ContService {
    private static ContService instance;
    private Map<String, Cont> conturi;
    private List<Tranzactie> istoricTranzactii;

    private ContService() {
        conturi = new HashMap<>();
        istoricTranzactii = new ArrayList<>();
    }

    public static ContService getInstance() {
        if (instance == null) {
            instance = new ContService();
        }
        return instance;
    }

    public void adaugaCont(Cont cont) {
        if (cont == null || cont.getIban() == null) return;
        conturi.put(cont.getIban(), cont);
        System.out.println("Cont deschis: " + cont.getIban() + " (" + cont.getTipCont() + ")");
    }

    public Cont gasesteCont(String iban) {
        return conturi.get(iban);
    }

    public void listeazaToateConturile() {
        System.out.println("Toate conturile ");
        for (Cont c : conturi.values()) {
            System.out.println(c.getIban() + " - Sold: " + c.getSold() + " RON [" + c.getTitular().getNume() + "]");
        }
    }

    public void depunere(String iban, double suma) {
        Cont cont = gasesteCont(iban);
        if (cont != null && suma > 0) {
            cont.depunere(suma);
            inregistreazaTranzactie(suma, "CREDIT (Depunere pe " + iban + ")");
            System.out.println("S-au depus " + suma + " RON in contul " + iban);
        }
    }

    public void retragere(String iban, double suma) throws FonduriInsuficienteException {
        Cont cont = gasesteCont(iban);
        if (cont != null) {
            if (cont.retragere(suma)) {
                inregistreazaTranzactie(suma, "DEBIT (Retragere din " + iban + ")");
                System.out.println("S-au retras " + suma + " RON din contul " + iban);
            } else {
                throw new FonduriInsuficienteException("Sold insuficient in contul: " + iban);
            }
        }
    }

    public void transfer(String ibanSursa, String ibanDestinatie, double suma) throws FonduriInsuficienteException {
        Cont sursa = gasesteCont(ibanSursa);
        Cont destinatie = gasesteCont(ibanDestinatie);

        if (sursa != null && destinatie != null) {
            if (sursa.retragere(suma)) {
                destinatie.depunere(suma);
                inregistreazaTranzactie(suma, "TRANSFER (" + ibanSursa + " -> " + ibanDestinatie + ")");
                System.out.println("Transfer reusit: " + suma + " RON către " + ibanDestinatie);
            } else {
                throw new FonduriInsuficienteException("Fonduri insuficiente pentru transfer pe contul sursa");
            }
        }
    }
    public double calculeazaAvereClient(Client client) {
        double total = 0;
        for (Cont c : conturi.values()) {
            if (c.getTitular().equals(client)) {
                total += c.getSold();
            }
        }
        return total;
    }

    private void inregistreazaTranzactie(double suma, String detalii) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Tranzactie t = new Tranzactie(id, suma, detalii);
        istoricTranzactii.add(t);
    }

    public void afiseazaIstoricTranzactii() {
        System.out.println("Istoric Tranzactii");
        for (Tranzactie t : istoricTranzactii) {
            System.out.println(t);
        }
    }
}