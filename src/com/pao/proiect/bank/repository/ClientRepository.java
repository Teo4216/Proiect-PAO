package com.pao.proiect.bank.repository;

import com.pao.proiect.bank.model.Client;
import com.pao.proiect.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClientRepository implements Repository<Client, String> {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // save
    @Override
    public void save(Client c) throws SQLException {
        String sql = "INSERT INTO clienti (cnp, nume, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getCnp());
            ps.setString(2, c.getNume());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();
        }
    }

    // findById
    @Override
    public Optional<Client> findById(String cnp) throws SQLException {
        String sql = "SELECT cnp, nume, email FROM clienti WHERE cnp = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cnp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    // findAll
    @Override
    public List<Client> findAll() throws SQLException {
        String sql = "SELECT cnp, nume, email FROM clienti ORDER BY nume";
        List<Client> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // update
    @Override
    public void update(Client c) throws SQLException {
        String sql = "UPDATE clienti SET nume = ?, email = ? WHERE cnp = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCnp());
            ps.executeUpdate();
        }
    }

    // delete
    @Override
    public void delete(String cnp) throws SQLException {
        String sql = "DELETE FROM clienti WHERE cnp = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cnp);
            ps.executeUpdate();
        }
    }

    // JOIN 1: clienti cu numarul de conturi deschise
    public List<String> clientiCuNumarConturi() throws SQLException {
        String sql =
            "SELECT c.nume, c.cnp, COUNT(co.iban) AS nr " +
            "FROM clienti c " +
            "LEFT JOIN conturi co ON c.cnp = co.client_cnp " +
            "GROUP BY c.cnp, c.nume ORDER BY nr DESC, c.nume";

        List<String> rez = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rez.add(String.format("%-25s (CNP: %s) — %d conturi",
                    rs.getString("nume"), rs.getString("cnp"), rs.getInt("nr")));
            }
        }
        return rez;
    }

    //  mapare ResultSet → Client
    private Client map(ResultSet rs) throws SQLException {
        return new Client(rs.getString("nume"),
                          rs.getString("cnp"),
                          rs.getString("email"));
    }
}
