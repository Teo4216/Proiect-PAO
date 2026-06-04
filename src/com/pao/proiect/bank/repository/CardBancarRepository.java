package com.pao.proiect.bank.repository;

import com.pao.proiect.bank.model.*;
import com.pao.proiect.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.*;


public class CardBancarRepository implements Repository<CardBancar, String> {

    private final ContRepository contRepo = new ContRepository();

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(CardBancar card) throws SQLException {
        String sql = "INSERT INTO carduri_bancare (numar_card, cont_iban) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, card.getNumarCard());
            ps.setString(2, card.getContAsociat().getIban());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<CardBancar> findById(String numarCard) throws SQLException {
        String sql = "SELECT numar_card, cont_iban " +
                     "FROM carduri_bancare WHERE numar_card = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, numarCard);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CardBancar> findAll() throws SQLException {
        String sql = "SELECT numar_card, cont_iban " +
                     "FROM carduri_bancare ORDER BY numar_card";
        List<CardBancar> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public void update(CardBancar card) throws SQLException {
        String sql = "UPDATE carduri_bancare SET cont_iban = ? WHERE numar_card = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, card.getContAsociat().getIban());
            ps.setString(2, card.getNumarCard());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String numarCard) throws SQLException {
        String sql = "DELETE FROM carduri_bancare WHERE numar_card = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, numarCard);
            ps.executeUpdate();
        }
    }

    private CardBancar map(ResultSet rs) throws SQLException {
        String numar = rs.getString("numar_card");
        String iban  = rs.getString("cont_iban");
        Cont cont = contRepo.findById(iban).orElseThrow(() ->
            new SQLException("Contul " + iban + " nu a fost gasit pentru cardul " + numar));
        return new CardBancar(numar, cont);
    }
}
