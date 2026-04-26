package com.pao.proiect.bank;

import com.pao.proiect.bank.exception.ClientInexistentException;
import com.pao.proiect.bank.exception.FonduriInsuficienteException;
import com.pao.proiect.bank.model.Client;
import com.pao.proiect.bank.model.Cont;
import com.pao.proiect.bank.model.ContCurent;
import com.pao.proiect.bank.model.ContEconomii;
import com.pao.proiect.bank.service.ClientService;
import com.pao.proiect.bank.service.ContService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Pornire aplicatie bancara\n");

        ClientService clientService = ClientService.getInstance();
        ContService contService = ContService.getInstance();

        try {
            System.out.println("1. Adăugare Clienți");
            Client client1 = new Client("Popescu Ion", "1900101123456", "ion@email.com");
            Client client2 = new Client("Ionescu Maria", "2920202123456", "maria@email.com");
            Client client3 = new Client("Avram Andrei", "1950303123456", "andrei@email.com");

            clientService.adaugaClient(client1);
            clientService.adaugaClient(client2);
            clientService.adaugaClient(client3);
            System.out.println("\n8. Listare Clienți");
            clientService.afiseazaTotiClientii();
            System.out.println("\n2 & 3. Deschidere Conturi");
            ContCurent contCurentIon = new ContCurent("RO12BTRL111", client1);
            ContEconomii contEconomiiIon = new ContEconomii("RO12BTRL999", client1, 5.0);
            ContCurent contCurentMaria = new ContCurent("RO45INGB222", client2);

            contService.adaugaCont(contCurentIon);
            contService.adaugaCont(contEconomiiIon);
            contService.adaugaCont(contCurentMaria);

            System.out.println("\n4. Depunere Numerar");
            contService.depunere("RO12BTRL111", 5000);
            contService.depunere("RO12BTRL999", 10000);
            contService.depunere("RO45INGB222", 2000);

            System.out.println("\n5. Retragere Numerar");
            contService.retragere("RO12BTRL111", 1500); // Ion retrage 1500 RON
            System.out.println("\n6. Transfer ");
            contService.transfer("RO12BTRL111", "RO45INGB222", 500); // Ion ii transfera Mariei 500 RON

            System.out.println("\n9. Cautare cont ");
            Cont contGasit = contService.gasesteCont("RO12BTRL111");
            if (contGasit != null) {
                System.out.println("Cont gasit: " + contGasit);
            }
            System.out.println("\n10. Calcul valoare cont client ");
            double avereIon = contService.calculeazaAvereClient(client1);
            System.out.println("Valoare totala " + client1.getNume() + " in toate conturile: " + avereIon + " RON");

            System.out.println("\n7. Istoric tranzactii");
            contService.afiseazaIstoricTranzactii();

            System.out.println("\n-Testare exceptii");
            try {
                contService.retragere("RO45INGB222", 999999);
            } catch (FonduriInsuficienteException e) {
                System.out.println("Exceptie: " + e.getMessage());
            }

            try {
                clientService.gasesteClientDupaCnp("0000000000000");
            } catch (ClientInexistentException e) {
                System.out.println("Excepție : " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("A aparut o eroare: " + e.getMessage());
        }

        System.out.println("\nFinalizare");
    }
}