package com.pao.proiect.bank.service;

import com.pao.proiect.bank.exception.ClientInexistentException;
import com.pao.proiect.bank.model.Client;

import java.util.*;


public class ClientService {

    private static ClientService instance;
    private final Set<Client> clienti;

    private ClientService() {
        clienti = new TreeSet<>(Comparator.comparing(Client::getNume));
    }

    public static ClientService getInstance() {
        if (instance == null) instance = new ClientService();
        return instance;
    }

    // ── Actiunea 1: Adauga client
    public void adaugaClient(Client client) {
        if (client == null || client.getCnp() == null) return;
        clienti.add(client);
        System.out.println("  Client adaugat: " + client.getNume());
        AuditService.getInstance().logAction("adauga_client");
    }

    // ── Actiunea: Sterge client
    public void stergeClient(String cnp) throws ClientInexistentException {
        Client target = gasesteClientDupaCnp(cnp);
        clienti.remove(target);
        System.out.println("  Client sters: CNP " + cnp);
        AuditService.getInstance().logAction("sterge_client");
    }

    // ── Actiunea: Cauta client dupa CNP
    public Client gasesteClientDupaCnp(String cnp) throws ClientInexistentException {
        for (Client c : clienti) {
            if (c.getCnp().equals(cnp)) {
                AuditService.getInstance().logAction("cauta_client_cnp");
                return c;
            }
        }
        throw new ClientInexistentException("Nu exista client cu CNP: " + cnp);
    }

    // ── Actiunea 8: Listeaza clienti
    public void afiseazaTotiClientii() {
        System.out.println("  Lista clienti (sortata dupa nume):");
        clienti.forEach(c -> System.out.println("    " + c));
        AuditService.getInstance().logAction("listare_clienti");
    }

    public Set<Client> getClienti() { return Collections.unmodifiableSet(clienti); }
}
