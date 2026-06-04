package com.pao.proiect.bank.repository;

import com.pao.proiect.bank.model.*;
import com.pao.proiect.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.*;


public class ContRepository implements Repository<Cont, String> {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    //  save
    @Override
    public void save(Cont c) throws SQLException {
        String sql = "INSERT INTO conturi (iban, tip, sold, dobanda, client_cnp) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getIban());
            ps.setString(2, c.getTipCont());
            ps.setDouble(3, c.getSold());
            ps.setDouble(4, c instanceof ContEconomii ? ((ContEconomii) c).getDobanda() : 0.0);
            ps.setString(5, c.getTitular().getCnp());
            ps.executeUpdate();
        }
    }

    // findById
    @Override
    public Optional<Cont> findById(String iban) throws SQLException {
        String sql =
            "SELECT co.iban, co.tip, co.sold, co.dobanda, " +
            "       c.cnp, c.nume, c.email " +
            "FROM conturi co JOIN clienti c ON co.client_cnp = c.cnp " +
            "WHERE co.iban = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, iban);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    //  findAll
    @Override
    public List<Cont> findAll() throws SQLException {
        String sql =
            "SELECT co.iban, co.tip, co.sold, co.dobanda, " +
            "       c.cnp, c.nume, c.email " +
            "FROM conturi co JOIN clienti c ON co.client_cnp = c.cnp " +
            "ORDER BY c.nume, co.iban";
        List<Cont> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    //  update
    @Override
    public void update(Cont c) throws SQLException {
        String sql = "UPDATE conturi SET sold = ?, dobanda = ? WHERE iban = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, c.getSold());
            ps.setDouble(2, c instanceof ContEconomii ? ((ContEconomii) c).getDobanda() : 0.0);
            ps.setString(3, c.getIban());
            ps.executeUpdate();
        }
    }

    //  delete
    @Override
    public void delete(String iban) throws SQLException {
        String sql = "DELETE FROM conturi WHERE iban = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, iban);
            ps.executeUpdate();
        }
    }


    public void updateSold(Connection extConn, String iban, double sold) throws SQLException {
        String sql = "UPDATE conturi SET sold = ? WHERE iban = ?";
        try (PreparedStatement ps = extConn.prepareStatement(sql)) {
            ps.setDouble(1, sold);
            ps.setString(2, iban);
            ps.executeUpdate();
        }
    }

    //  JOIN 2: conturi cu detalii client
    public List<String> conturiCuDetaliiClient() throws SQLException {
        String sql =
            "SELECT co.iban, co.tip, co.sold, co.dobanda, c.nume, c.email " +
            "FROM conturi co JOIN clienti c ON co.client_cnp = c.cnp " +
            "ORDER BY c.nume, co.tip";
        List<String> rez = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String dob = "Economii".equals(rs.getString("tip"))
                    ? String.format(" (dob: %.1f%%)", rs.getDouble("dobanda")) : "";
                rez.add(String.format("[%s] %-8s | %10.2f RON%s | %s <%s>",
                    rs.getString("iban"), rs.getString("tip"),
                    rs.getDouble("sold"), dob,
                    rs.getString("nume"), rs.getString("email")));
            }
        }
        return rez;
    }

    // JOIN 3: tranzactii cu detalii cont + client
    public List<String> tranzactiiCuDetaliiContSiClient() throws SQLException {
        String sql =
            "SELECT t.id, t.suma, t.tip AS tip_t, t.data, " +
            "       co.iban, co.tip AS tip_c, c.nume " +
            "FROM tranzactii t " +
            "JOIN conturi co ON t.cont_iban = co.iban " +
            "JOIN clienti c  ON co.client_cnp = c.cnp " +
            "ORDER BY t.data DESC";
        List<String> rez = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rez.add(String.format("[%s] %9.2f RON | %-40s | %s (%s) | %s",
                    rs.getString("id"), rs.getDouble("suma"),
                    rs.getString("tip_t"),
                    rs.getString("iban"), rs.getString("tip_c"),
                    rs.getString("nume")));
            }
        }
        return rez;
    }

    //  mapare ResultSet → Cont
    private Cont map(ResultSet rs) throws SQLException {
        Client client = new Client(rs.getString("nume"),
                                   rs.getString("cnp"),
                                   rs.getString("email"));
        Cont cont;
        if ("Economii".equals(rs.getString("tip"))) {
            cont = new ContEconomii(rs.getString("iban"), client, rs.getDouble("dobanda"));
        } else {
            cont = new ContCurent(rs.getString("iban"), client);
        }
        cont.setSold(rs.getDouble("sold"));
        return cont;
    }
}
