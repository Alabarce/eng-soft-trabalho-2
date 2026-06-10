package com.estacionamento.web;

import com.estacionamento.dao.TicketDAO;
import com.estacionamento.domain.Estacionamento;
import com.estacionamento.domain.Ticket;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        String dbPath = System.getenv().getOrDefault("DB_PATH", "estacionamento.db");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7000"));

        TicketDAO ticketDAO = new TicketDAO(dbPath);
        Estacionamento est = new Estacionamento(10, 10, ticketDAO);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(port);

        app.get("/api/ocupacao", ctx -> {
            Map<String, Object> resp = new HashMap<>();
            resp.put("carroLivres", est.vagasLivresCarro());
            resp.put("carroTotal", est.vagasTotalCarro());
            resp.put("motoLivres", est.vagasLivresMoto());
            resp.put("motoTotal", est.vagasTotalMoto());
            ctx.json(resp);
        });

        app.post("/api/entrada", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String placa = body.get("placa");
            String tipo = body.get("tipo");

            if (placa == null || placa.isBlank() || tipo == null) {
                ctx.status(400).json(erro("Placa e tipo são obrigatórios."));
                return;
            }

            Ticket t = est.registrarEntrada(placa.trim(), tipo.trim().toUpperCase());
            if (t == null) {
                ctx.status(409).json(erro("Estacionamento lotado para " + tipo.toLowerCase() + "."));
                return;
            }
            ctx.json(ticketParaMap(t));
        });

        app.post("/api/saida", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String placa = body.get("placa");

            if (placa == null || placa.isBlank()) {
                ctx.status(400).json(erro("Placa é obrigatória."));
                return;
            }

            Ticket t = est.registrarSaida(placa.trim());
            if (t == null) {
                ctx.status(404).json(erro("Veículo não encontrado ou sem ticket aberto."));
                return;
            }
            ctx.json(ticketParaMap(t));
        });

        app.get("/api/consulta/{placa}", ctx -> {
            String placa = ctx.pathParam("placa");
            Optional<Ticket> opt = est.consultarPorPlaca(placa);
            if (opt.isEmpty()) {
                ctx.status(404).json(erro("Nenhum ticket aberto para esta placa."));
                return;
            }
            ctx.json(ticketParaMap(opt.get()));
        });

        app.get("/api/historico", ctx -> {
            List<Map<String, Object>> lista = est.historico()
                .stream()
                .map(App::ticketParaMap)
                .collect(java.util.stream.Collectors.toList());
            ctx.json(lista);
        });
    }

    private static Map<String, Object> ticketParaMap(Ticket t) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", t.getId());
        m.put("placa", t.getVeiculo().getPlaca());
        m.put("tipo", t.getVeiculo().getTipo());
        m.put("vaga", t.getVaga().getNumero());
        m.put("entrada", t.getEntrada().format(FMT));
        m.put("saida", t.getSaida() != null ? t.getSaida().format(FMT) : null);
        m.put("valor", t.getSaida() != null ? String.format("%.2f", t.getValor()) : null);
        m.put("aberto", t.isAberto());
        return m;
    }

    private static Map<String, String> erro(String msg) {
        return Map.of("erro", msg);
    }
}
