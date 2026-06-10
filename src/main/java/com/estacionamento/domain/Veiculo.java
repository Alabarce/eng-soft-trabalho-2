package com.estacionamento.domain;

public abstract class Veiculo {
    private final String placa;

    public Veiculo(String placa) {
        this.placa = placa.toUpperCase().trim();
    }

    public String getPlaca() { return placa; }

    public abstract double getTaxaPorHora();

    public abstract String getTipo();
}
