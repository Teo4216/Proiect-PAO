package com.pao.proiect.bank.service;

import com.pao.proiect.bank.exception.ClientInexistentException;
import com.pao.proiect.bank.model.Client;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class ClientService {
    private static ClientService instance;

    private Set<Client> clienti;

    private ClientService() {
        clienti = new TreeSet<>(Comparator.comparing(Client::getNume));
    }

    public static ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void adaugaClient(Client client) {
        if (client == null || client.getCnp() == null) return;
        clienti.add(client);
        System.out.println("Client adaugat: " + client.getNume());
    }

    public void stergeClient(String cnp) throws ClientInexistentException {
        Client target = gasesteClientDupaCnp(cnp);
        clienti.remove(target);
        System.out.println("Clientul cu CNP " + cnp + " a fost sters.");
    }
    public Client gasesteClientDupaCnp(String cnp) throws ClientInexistentException {
        for (Client c : clienti) {
            if (c.getCnp().equals(cnp)) {
                return c;
            }
        }
        throw new ClientInexistentException("Nu există niciun client cu CNP-ul: " + cnp);
    }

    public void afiseazaTotiClientii() {
        System.out.println("Lista clienti");
        for (Client c : clienti) {
            System.out.println(c);
        }
    }
}