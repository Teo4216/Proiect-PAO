package com.pao.proiect.bank.repository;

import com.pao.proiect.bank.model.Tranzactie;
import com.pao.proiect.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.*;


public class TranzactieRepository implements Repository<Tranzactie, String> {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    //  save
    @Override
    public void save(Tranzactie t) throws SQLException {
        String sql = "INSERT INTO tranzactii (id, suma, tip, data, cont_iban) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setDouble(2, t.getSuma());
            ps.setString(3, t.getTip());
            ps.setString(4, t.getData().toString());
            if (t.getContIban() != null) ps.setString(5, t.getContIban());
            else                          ps.setNull(5, Types.VARCHAR);
            ps.executeUpdate();
        }
    }


    public void save(Connection extConn, Tranzactie t) throws SQLException {
        String sql = "INSERT INTO tranzactii (id, suma, tip, data, cont_iban) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = extConn.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setDouble(2, t.getSuma());
            ps.setString(3, t.getTip());
            ps.setString(4, t.getData().toString());
            if (t.getContIban() != null) ps.setString(5, t.getContIban());
            else                          ps.setNull(5, Types.VARCHAR);
            ps.executeUpdate();
        }
    }

    //  findById
    @Override
    public Optional<Tranzactie> findById(String id) throws SQLException {
        String sql = "SELECT id, suma, tip, data, cont_iban " +
                     "FROM tranzactii WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    // findAll
    @Override
    public List<Tranzactie> findAll() throws SQLException {
        String sql = "SELECT id, suma, tip, data, cont_iban " +
                     "FROM tranzactii ORDER BY data DESC";
        List<Tranzactie> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    //  update — tranzactiile sunt imutabile
    @Override
    public void update(Tranzactie t) {
        throw new UnsupportedOperationException(
            "Tranzactiile sunt imutabile si nu pot fi modificate.");
    }

    //  delete
    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM tranzactii WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }


    public List<Tranzactie> findByContIban(String iban) throws SQLException {
        String sql = "SELECT id, suma, tip, data, cont_iban " +
                     "FROM tranzactii WHERE cont_iban = ? ORDER BY data DESC";
        List<Tranzactie> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, iban);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Tranzactie map(ResultSet rs) throws SQLException {
        return new Tranzactie(rs.getString("id"),
                              rs.getDouble("suma"),
                              rs.getString("tip"),
                              rs.getString("cont_iban"));
    }
}
