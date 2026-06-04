package com.pao.proiect.bank.service;

import com.pao.proiect.bank.exception.FonduriInsuficienteException;
import com.pao.proiect.bank.model.*;
import com.pao.proiect.bank.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class ContService {

    private static ContService instance;
    private final Map<String, Cont>  conturi;
    private final List<Tranzactie>   istoricTranzactii;

    private ContService() {
        conturi           = new HashMap<>();
        istoricTranzactii = new ArrayList<>();
    }

    public static ContService getInstance() {
        if (instance == null) instance = new ContService();
        return instance;
    }

    // ─ Actiunile 2 & 3: Deschide cont
    public void adaugaCont(Cont cont) {
        if (cont == null || cont.getIban() == null) return;
        conturi.put(cont.getIban(), cont);
        System.out.println("  Cont deschis: " + cont.getIban() +
                           " (" + cont.getTipCont() + ")");
        AuditService.getInstance().logAction(
            "deschide_cont_" + cont.getTipCont().toLowerCase());
    }

    // ─ Actiunea 9: Cauta cont
    public Cont gasesteCont(String iban) {
        AuditService.getInstance().logAction("cauta_cont");
        return conturi.get(iban);
    }

    // ─ Actiunea 4: Depunere numerar
    public void depunere(String iban, double suma) {
        Cont cont = conturi.get(iban);
        if (cont != null && suma > 0) {
            cont.depunere(suma);
            inregistreazaTranzactie(suma,
                "CREDIT (Depunere pe " + iban + ")", iban);
            System.out.println("  Depus " + suma + " RON in " + iban);
        }
        AuditService.getInstance().logAction("depunere");
    }

    // ─ Actiunea 5: Retragere numerar
    public void retragere(String iban, double suma)
            throws FonduriInsuficienteException {
        Cont cont = conturi.get(iban);
        if (cont != null) {
            if (cont.retragere(suma)) {
                inregistreazaTranzactie(suma,
                    "DEBIT (Retragere din " + iban + ")", iban);
                System.out.println("  Retras " + suma + " RON din " + iban);
            } else {
                throw new FonduriInsuficienteException(
                    "Sold insuficient in contul: " + iban);
            }
        }
        AuditService.getInstance().logAction("retragere");
    }

    // ─ Actiunea 6: Transfer in-memory
    public void transfer(String ibanSursa, String ibanDest, double suma)
            throws FonduriInsuficienteException {
        Cont sursa = conturi.get(ibanSursa);
        Cont dest  = conturi.get(ibanDest);
        if (sursa != null && dest != null) {
            if (sursa.retragere(suma)) {
                dest.depunere(suma);
                inregistreazaTranzactie(suma,
                    "TRANSFER (" + ibanSursa + " -> " + ibanDest + ")", ibanSursa);
                System.out.println("  Transfer " + suma + " RON: " +
                                   ibanSursa + " -> " + ibanDest);
            } else {
                throw new FonduriInsuficienteException(
                    "Fonduri insuficiente in contul sursa: " + ibanSursa);
            }
        }
        AuditService.getInstance().logAction("transfer");
    }

    //  TRANZACTIE JDBC
    public void transferCuTranzactieJDBC(String ibanSursa, String ibanDest,
                                         double suma)
            throws FonduriInsuficienteException, SQLException {

        Cont sursa = conturi.get(ibanSursa);
        Cont dest  = conturi.get(ibanDest);

        if (sursa == null || dest == null) {
            throw new IllegalArgumentException("IBAN invalid: " +
                (sursa == null ? ibanSursa : ibanDest));
        }
        if (!sursa.retragere(suma)) {
            throw new FonduriInsuficienteException(
                "Fonduri insuficiente in: " + ibanSursa);
        }
        dest.depunere(suma);

        String idTrz = UUID.randomUUID().toString().substring(0, 8);
        Tranzactie trz = new Tranzactie(idTrz, suma,
            "TRANSFER_JDBC (" + ibanSursa + " -> " + ibanDest + ")", ibanSursa);
        istoricTranzactii.add(trz);

        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);
        try {
            // Operatia 1: scade sold sursa
            String sqlUpd = "UPDATE conturi SET sold = ? WHERE iban = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setDouble(1, sursa.getSold());
                ps.setString(2, ibanSursa);
                ps.executeUpdate();
            }
            // Operatia 2: creste sold destinatie
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setDouble(1, dest.getSold());
                ps.setString(2, ibanDest);
                ps.executeUpdate();
            }
            // Operatia 3: insereaza tranzactia
            String sqlIns =
                "INSERT INTO tranzactii (id, suma, tip, data, cont_iban) " +
                "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlIns)) {
                ps.setString(1, idTrz);
                ps.setDouble(2, suma);
                ps.setString(3, trz.getTip());
                ps.setString(4, LocalDateTime.now().toString());
                ps.setString(5, ibanSursa);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("  [JDBC] Transfer comis: " + suma + " RON (" +
                               ibanSursa + " -> " + ibanDest + ")");

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("  [JDBC] Eroare — rollback efectuat: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        AuditService.getInstance().logAction("transfer_jdbc");
    }

    // ─ Actiunea 10: Calculeaza averea unui client
    public double calculeazaAvereClient(Client client) {
        double total = conturi.values().stream()
            .filter(c -> c.getTitular().equals(client))
            .mapToDouble(Cont::getSold)
            .sum();
        AuditService.getInstance().logAction("calcul_avere_client");
        return total;
    }

    // ─ Actiunea 7: Afisare istoric tranzactii
    public void afiseazaIstoricTranzactii() {
        System.out.println("  Istoric tranzactii (" +
                           istoricTranzactii.size() + " inregistrari):");
        istoricTranzactii.forEach(t -> System.out.println("    " + t));
        AuditService.getInstance().logAction("afisare_tranzactii");
    }

    public List<Tranzactie> getIstoricTranzactii() {
        return Collections.unmodifiableList(istoricTranzactii);
    }

    // ─ inregistrare tranzactie in-memory
    private void inregistreazaTranzactie(double suma, String tip, String iban) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        istoricTranzactii.add(new Tranzactie(id, suma, tip, iban));
    }
}
