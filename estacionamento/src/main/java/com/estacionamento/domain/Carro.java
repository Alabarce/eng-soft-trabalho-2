package com.estacionamento.domain;

public class Carro extends Veiculo {
    public Carro(String placa) { super(placa); }

    @Override
    public double getTaxaPorHora() { return 5.0; }

    @Override
    public String getTipo() { return "CARRO"; }
}
