package com.estacionamento.domain;

public class Vaga {
    public enum Tipo { CARRO, MOTO }

    private final int numero;
    private final Tipo tipo;
    private boolean ocupada;

    public Vaga(int numero, Tipo tipo) {
        this.numero = numero;
        this.tipo = tipo;
        this.ocupada = false;
    }

    public int getNumero() { return numero; }
    public Tipo getTipo() { return tipo; }
    public boolean isOcupada() { return ocupada; }

    public void ocupar() { this.ocupada = true; }
    public void desocupar() { this.ocupada = false; }
}
