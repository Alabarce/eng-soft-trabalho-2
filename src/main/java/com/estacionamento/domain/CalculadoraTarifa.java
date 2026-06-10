package com.estacionamento.domain;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Calcula o valor a pagar com base no tipo do veículo e no tempo de permanência.
 * Regra: hora cheia ou fração (Math.ceil), conforme RN01.
 */
public class CalculadoraTarifa {

    public double calcular(Veiculo veiculo, LocalDateTime entrada, LocalDateTime saida) {
        long minutos = Duration.between(entrada, saida).toMinutes();
        if (minutos <= 0) return 0.0;
        double horas = Math.ceil(minutos / 60.0);
        return horas * veiculo.getTaxaPorHora();
    }
}
