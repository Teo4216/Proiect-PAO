package com.pao.proiect.bank;

import com.pao.proiect.bank.exception.ClientInexistentException;
import com.pao.proiect.bank.exception.FonduriInsuficienteException;
import com.pao.proiect.bank.model.*;
import com.pao.proiect.bank.repository.*;
import com.pao.proiect.bank.service.ClientService;
import com.pao.proiect.bank.service.ContService;
import com.pao.proiect.bank.util.DatabaseInitializer;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("   APLICATIE BANCARA — Etapa I + Etapa II");

        ClientService clientService = ClientService.getInstance();
        ContService   contService   = ContService.getInstance();
        //   ETAPA I — Operatii in memorie (toate cele 10 actiuni)
        System.out.println("ETAPA I: Operatii in memorie \n");
        try {
            // 1. Adaugare Clienti
            System.out.println("[1] Adaugare Clienti");
            Client c1 = new Client("Popescu Ion",   "1900101123456", "ion@email.com");
            Client c2 = new Client("Ionescu Maria", "2920202123456", "maria@email.com");
            Client c3 = new Client("Avram Andrei",  "1950303123456", "andrei@email.com");
            clientService.adaugaClient(c1);
            clientService.adaugaClient(c2);
            clientService.adaugaClient(c3);

            // 8. Listare Clienti
            System.out.println("\n[8] Listare Clienti");
            clientService.afiseazaTotiClientii();

            // 2 & 3. Deschidere Conturi
            System.out.println("\n[2&3] Deschidere Conturi");
            ContCurent   cc1 = new ContCurent  ("RO12BTRL111", c1);
            ContEconomii ce1 = new ContEconomii("RO12BTRL999", c1, 5.0);
            ContCurent   cc2 = new ContCurent  ("RO45INGB222", c2);
            contService.adaugaCont(cc1);
            contService.adaugaCont(ce1);
            contService.adaugaCont(cc2);

            // 4. Depunere
            System.out.println("\n[4] Depunere Numerar");
            contService.depunere("RO12BTRL111", 5000);
            contService.depunere("RO12BTRL999", 10000);
            contService.depunere("RO45INGB222", 2000);

            // 5. Retragere
            System.out.println("\n[5] Retragere Numerar");
            contService.retragere("RO12BTRL111", 1500);

            // 6. Transfer
            System.out.println("\n[6] Transfer");
            contService.transfer("RO12BTRL111", "RO45INGB222", 500);

            // 9. Cautare cont
            System.out.println("\n[9] Cautare Cont");
            Cont gasit = contService.gasesteCont("RO12BTRL111");
            if (gasit != null) System.out.println("  Gasit: " + gasit);

            // 10. Calcul avere
            System.out.println("\n[10] Calcul Avere Client");
            double avere = contService.calculeazaAvereClient(c1);
            System.out.println("  Avere totala " + c1.getNume() + ": " + avere + " RON");

            // 7. Istoric tranzactii
            System.out.println("\n[7] Istoric Tranzactii");
            contService.afiseazaIstoricTranzactii();

            // Testare exceptii
            System.out.println("\n[Exceptii]");
            try {
                contService.retragere("RO45INGB222", 999999);
            } catch (FonduriInsuficienteException e) {
                System.out.println("  FonduriInsuficiente: " + e.getMessage());
            }
            try {
                clientService.gasesteClientDupaCnp("0000000000000");
            } catch (ClientInexistentException e) {
                System.out.println("  ClientInexistent: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Eroare Etapa I: " + e.getMessage());
        }

        //   ETAPA II
        System.out.println("\n ETAPA II: \n");

        try {
            // Initializeaza schema (DROP + CREATE)
            DatabaseInitializer.initializeSchema();

            ClientRepository     clientRepo = new ClientRepository();
            ContRepository       contRepo   = new ContRepository();
            TranzactieRepository trzRepo    = new TranzactieRepository();
            CardBancarRepository cardRepo   = new CardBancarRepository();

            // CRUD save()
            System.out.println("\n[CRUD] save()");
            Client d1 = new Client("Popescu Ion",   "1900101123456", "ion@email.com");
            Client d2 = new Client("Ionescu Maria", "2920202123456", "maria@email.com");
            Client d3 = new Client("Avram Andrei",  "1950303123456", "andrei@email.com");
            clientRepo.save(d1);
            clientRepo.save(d2);
            clientRepo.save(d3);
            System.out.println("  3 clienti salvati in DB.");

            ContCurent   dc1 = new ContCurent  ("RO12BTRL111", d1);
            ContEconomii de1 = new ContEconomii("RO12BTRL999", d1, 5.0);
            ContCurent   dc2 = new ContCurent  ("RO45INGB222", d2);
            dc1.depunere(3500); de1.depunere(10000); dc2.depunere(1500);
            contRepo.save(dc1);
            contRepo.save(de1);
            contRepo.save(dc2);
            System.out.println("  3 conturi salvate in DB.");

            CardBancar card = new CardBancar("4111-1111-1111-1111", dc1);
            cardRepo.save(card);
            System.out.println("  1 card salvat in DB.");

            // ─ CRUD findAll()
            System.out.println("\n[CRUD] findAll() — clienti");
            clientRepo.findAll().forEach(c -> System.out.println("  " + c));

            System.out.println("\n[CRUD] findAll() — conturi");
            contRepo.findAll().forEach(c -> System.out.println("  " + c));

            // ─ CRUD findById()
            System.out.println("\n[CRUD] findById()");
            clientRepo.findById("1900101123456")
                .ifPresent(c -> System.out.println("  Client gasit: " + c));
            contRepo.findById("RO12BTRL999")
                .ifPresent(c -> System.out.println("  Cont gasit:   " + c));

            // ─ CRUD update()
            System.out.println("\n[CRUD] update()");
            d2.setEmail("maria.updated@email.com");
            clientRepo.update(d2);
            clientRepo.findById("2920202123456")
                .ifPresent(c -> System.out.println("  Dupa update: " + c));

            // ─ CRUD delete()
            System.out.println("\n[CRUD] delete()");
            Client tmp = new Client("Temp Test", "9999999999999", "tmp@test.com");
            clientRepo.save(tmp);
            System.out.println("  Inainte: " + clientRepo.findAll().size() + " clienti");
            clientRepo.delete("9999999999999");
            System.out.println("  Dupa:    " + clientRepo.findAll().size() + " clienti");

            // ─ TRANZACTIE JDBC EXPLICITA
            System.out.println("\n[TRANZACTIE JDBC]");
            ContService cs = ContService.getInstance();
            cs.adaugaCont(dc1);
            cs.adaugaCont(dc2);
            // Reseteaza starea in-memory sa fie sincronizata cu DB
            dc1.setSold(3500); dc2.setSold(1500);
            cs.transferCuTranzactieJDBC("RO12BTRL111", "RO45INGB222", 800);
            System.out.println("  Sold RO12BTRL111: " + dc1.getSold() + " RON");
            System.out.println("  Sold RO45INGB222: " + dc2.getSold() + " RON");

            // Salveaza tranzactiile in-memory in DB (cele dinaintea JDBC)
            for (Tranzactie t : cs.getIstoricTranzactii()) {
                try { trzRepo.save(t); } catch (Exception ignored) {}
            }

            // ─Interogari cu join
            System.out.println("\n[JOIN 1] Clienti cu numarul de conturi deschise");
            clientRepo.clientiCuNumarConturi()
                .forEach(s -> System.out.println("  " + s));

            System.out.println("\n[JOIN 2] Conturi cu detalii client");
            contRepo.conturiCuDetaliiClient()
                .forEach(s -> System.out.println("  " + s));

            System.out.println("\n[JOIN 3] Tranzactii cu detalii cont + client");
            List<String> trzJoin = contRepo.tranzactiiCuDetaliiContSiClient();
            if (trzJoin.isEmpty()) System.out.println("  (nicio tranzactie cu cont asociat)");
            else trzJoin.forEach(s -> System.out.println("  " + s));

        } catch (Exception e) {
            System.err.println("Eroare Etapa II: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("  Audit: audit.csv  |  DB: paoj_banca.db");
    }
}
