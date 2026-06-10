package com.estacionamento.domain;

public class Moto extends Veiculo {
    public Moto(String placa) { super(placa); }

    @Override
    public double getTaxaPorHora() { return 3.0; }

    @Override
    public String getTipo() { return "MOTO"; }
}
