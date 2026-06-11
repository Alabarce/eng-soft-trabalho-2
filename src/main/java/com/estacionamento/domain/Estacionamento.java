package com.estacionamento.domain;

import com.estacionamento.dao.TicketDAO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gerencia vagas e coordena entradas/saídas.
 * Estado de vagas é mantido em memória; tickets são persistidos via TicketDAO.
 */
public class Estacionamento {

    private final List<Vaga> vagas = new ArrayList<>();
    private final TicketDAO ticketDAO;
    private final CalculadoraTarifa calculadora = new CalculadoraTarifa();

    public Estacionamento(int vagasCarro, int vagasMoto, TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;

        for (int i = 1; i <= vagasCarro; i++)
            vagas.add(new Vaga(i, Vaga.Tipo.CARRO));
        for (int i = vagasCarro + 1; i <= vagasCarro + vagasMoto; i++)
            vagas.add(new Vaga(i, Vaga.Tipo.MOTO));

        sincronizarVagasComBanco();
    }

    /** Marca vagas como ocupadas com base nos tickets abertos persistidos. */
    private void sincronizarVagasComBanco() {
        List<Ticket> abertos = ticketDAO.listarAbertos();
        for (Ticket t : abertos) {
            vagas.stream()
                .filter(v -> v.getNumero() == t.getVaga().getNumero())
                .findFirst()
                .ifPresent(Vaga::ocupar);
        }
    }

    public Ticket registrarEntrada(String placa, String tipo) {
        Vaga.Tipo tipoVaga = tipo.equalsIgnoreCase("CARRO") ? Vaga.Tipo.CARRO : Vaga.Tipo.MOTO;
        Veiculo veiculo = tipoVaga == Vaga.Tipo.CARRO ? new Carro(placa) : new Moto(placa);

        Optional<Vaga> vagaLivre = vagas.stream()
            .filter(v -> !v.isOcupada() && v.getTipo() == tipoVaga)
            .findFirst();

        if (vagaLivre.isEmpty()) return null;

        Vaga vaga = vagaLivre.get();
        vaga.ocupar();

        Ticket ticket = ticketDAO.salvar(veiculo, vaga, LocalDateTime.now());
        return ticket;
    }

    public Ticket registrarSaida(String placa) {
        Optional<Ticket> ticketOpt = ticketDAO.buscarAbertoPorPlaca(placa);
        if (ticketOpt.isEmpty()) return null;

        Ticket ticket = ticketOpt.get();
        LocalDateTime saida = LocalDateTime.now();
        double valor = calculadora.calcular(ticket.getVeiculo(), ticket.getEntrada(), saida);

        ticket.fechar(saida, valor);
        ticketDAO.fechar(ticket);

        vagas.stream()
            .filter(v -> v.getNumero() == ticket.getVaga().getNumero())
            .findFirst()
            .ifPresent(Vaga::desocupar);

        return ticket;
    }

    public Optional<Ticket> consultarPorPlaca(String placa) {
        return ticketDAO.buscarAbertoPorPlaca(placa);
    }

    public long vagasLivresCarro() {
        return vagas.stream().filter(v -> v.getTipo() == Vaga.Tipo.CARRO && !v.isOcupada()).count();
    }

    public long vagasLivresMoto() {
        return vagas.stream().filter(v -> v.getTipo() == Vaga.Tipo.MOTO && !v.isOcupada()).count();
    }

    public long vagasTotalCarro() {
        return vagas.stream().filter(v -> v.getTipo() == Vaga.Tipo.CARRO).count();
    }

    public long vagasTotalMoto() {
        return vagas.stream().filter(v -> v.getTipo() == Vaga.Tipo.MOTO).count();
    }

    public List<Ticket> historico() {
        return ticketDAO.listarFechados();
    }

    public List<Map<String, Object>> vagasComStatus() {
        Map<Integer, String> vagaPlaca = new HashMap<>();
        for (Ticket t : ticketDAO.listarAbertos())
            vagaPlaca.put(t.getVaga().getNumero(), t.getVeiculo().getPlaca());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Vaga v : vagas) {
            Map<String, Object> m = new HashMap<>();
            m.put("numero", v.getNumero());
            m.put("tipo", v.getTipo().name());
            m.put("ocupada", v.isOcupada());
            if (v.isOcupada()) m.put("placa", vagaPlaca.get(v.getNumero()));
            result.add(m);
        }
        return result;
    }
}
