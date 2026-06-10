package com.estacionamento.domain;

import java.time.LocalDateTime;

public class Ticket {
    private final int id;
    private final Veiculo veiculo;
    private final Vaga vaga;
    private final LocalDateTime entrada;
    private LocalDateTime saida;
    private double valor;

    public Ticket(int id, Veiculo veiculo, Vaga vaga, LocalDateTime entrada) {
        this.id = id;
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.entrada = entrada;
    }

    public int getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public Vaga getVaga() { return vaga; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSaida() { return saida; }
    public double getValor() { return valor; }
    public boolean isAberto() { return saida == null; }

    public void fechar(LocalDateTime saida, double valor) {
        this.saida = saida;
        this.valor = valor;
    }
}
