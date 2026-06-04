package com.pao.proiect.bank.util;

import java.io.*;
import java.sql.*;

public class DatabaseInitializer {

    private DatabaseInitializer() {}

    public static void initializeSchema() throws SQLException, IOException {
        String sql = citesteFisier("schema.sql");
        Connection conn = DatabaseConnection.getInstance().getConnection();

        StringBuilder faragComentarii = new StringBuilder();
        for (String linie : sql.split("\n")) {
            if (!linie.trim().startsWith("--")) {
                faragComentarii.append(linie).append("\n");
            }
        }

        for (String stmt : faragComentarii.toString().split(";")) {
            String trimmed = stmt.trim();
            if (!trimmed.isEmpty()) {
                try (Statement st = conn.createStatement()) {
                    st.execute(trimmed);
                }
            }
        }
        System.out.println("[DB] Schema initializata cu succes.");
    }

    private static String citesteFisier(String nume) throws IOException {
        try (InputStream is = DatabaseInitializer.class
                .getClassLoader().getResourceAsStream(nume)) {
            if (is == null) throw new IOException(
                "Fisierul '" + nume + "' nu a fost gasit in classpath.");
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linie;
                while ((linie = br.readLine()) != null)
                    sb.append(linie).append("\n");
            }
            return sb.toString();
        }
    }
}
