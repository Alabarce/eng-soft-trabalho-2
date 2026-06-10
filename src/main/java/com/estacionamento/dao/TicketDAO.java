package com.estacionamento.dao;

import com.estacionamento.domain.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDAO {

    private final String url;

    public TicketDAO(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        inicializar();
    }

    private void inicializar() {
        String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "placa TEXT NOT NULL," +
            "tipo TEXT NOT NULL," +
            "numero_vaga INTEGER NOT NULL," +
            "entrada TEXT NOT NULL," +
            "saida TEXT," +
            "valor REAL" +
        ")";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco", e);
        }
    }

    public Ticket salvar(Veiculo veiculo, Vaga vaga, LocalDateTime entrada) {
        String sql = "INSERT INTO tickets (placa, tipo, numero_vaga, entrada) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, veiculo.getPlaca());
            ps.setString(2, veiculo.getTipo());
            ps.setInt(3, vaga.getNumero());
            ps.setString(4, entrada.toString());
            ps.executeUpdate();
            int id = -1;
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) id = rs.getInt(1);
            }
            return new Ticket(id, veiculo, vaga, entrada);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ticket", e);
        }
    }

    public void fechar(Ticket ticket) {
        String sql = "UPDATE tickets SET saida = ?, valor = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticket.getSaida().toString());
            ps.setDouble(2, ticket.getValor());
            ps.setInt(3, ticket.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar ticket", e);
        }
    }

    public Optional<Ticket> buscarAbertoPorPlaca(String placa) {
        String sql = "SELECT * FROM tickets WHERE placa = ? AND saida IS NULL LIMIT 1";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, placa.toUpperCase().trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ticket", e);
        }
        return Optional.empty();
    }

    public List<Ticket> listarAbertos() {
        return listar("SELECT * FROM tickets WHERE saida IS NULL");
    }

    public List<Ticket> listarFechados() {
        return listar("SELECT * FROM tickets WHERE saida IS NOT NULL ORDER BY saida DESC");
    }

    private List<Ticket> listar(String sql) {
        List<Ticket> resultado = new ArrayList<>();
        try (Connection conn = connect();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) resultado.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tickets", e);
        }
        return resultado;
    }

    private Ticket mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String placa = rs.getString("placa");
        int numeroVaga = rs.getInt("numero_vaga");
        LocalDateTime entrada = LocalDateTime.parse(rs.getString("entrada"));
        String saidaStr = rs.getString("saida");
        double valor = rs.getDouble("valor");

        Veiculo veiculo = tipo.equals("CARRO") ? new Carro(placa) : new Moto(placa);
        Vaga vaga = new Vaga(numeroVaga, tipo.equals("CARRO") ? Vaga.Tipo.CARRO : Vaga.Tipo.MOTO);
        Ticket ticket = new Ticket(id, veiculo, vaga, entrada);

        if (saidaStr != null) {
            ticket.fechar(LocalDateTime.parse(saidaStr), valor);
        }
        return ticket;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }
}