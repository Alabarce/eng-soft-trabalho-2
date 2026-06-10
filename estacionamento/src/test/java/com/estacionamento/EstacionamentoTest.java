package com.estacionamento;

import com.estacionamento.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EstacionamentoTest {

    // ---- CalculadoraTarifa ----

    @Test
    void tarifaCarroUmaHoraExata() {
        CalculadoraTarifa calc = new CalculadoraTarifa();
        Veiculo carro = new Carro("TST0001");
        LocalDateTime entrada = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime saida   = LocalDateTime.of(2024, 1, 1, 11, 0);
        assertEquals(5.0, calc.calcular(carro, entrada, saida));
    }

    @Test
    void tarifaCarroFracaoCobraHoraCheia() {
        CalculadoraTarifa calc = new CalculadoraTarifa();
        Veiculo carro = new Carro("TST0002");
        LocalDateTime entrada = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime saida   = LocalDateTime.of(2024, 1, 1, 10, 30); // 30 min = 1 hora cobrada
        assertEquals(5.0, calc.calcular(carro, entrada, saida));
    }

    @Test
    void tarifaMotoTresHoras() {
        CalculadoraTarifa calc = new CalculadoraTarifa();
        Veiculo moto = new Moto("TST0003");
        LocalDateTime entrada = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime saida   = LocalDateTime.of(2024, 1, 1, 11, 0);
        assertEquals(9.0, calc.calcular(moto, entrada, saida));
    }

    @Test
    void tarifaZeroParaSaidaImediata() {
        CalculadoraTarifa calc = new CalculadoraTarifa();
        Veiculo carro = new Carro("TST0004");
        LocalDateTime agora = LocalDateTime.now();
        assertEquals(0.0, calc.calcular(carro, agora, agora));
    }

    // ---- Taxas ----

    @Test
    void carroTaxaCincoReais() {
        assertEquals(5.0, new Carro("TST0005").getTaxaPorHora());
    }

    @Test
    void motoTaxaTresReais() {
        assertEquals(3.0, new Moto("TST0006").getTaxaPorHora());
    }

    // ---- Ticket ----

    @Test
    void ticketAbertoAoInstanciar() {
        Vaga vaga = new Vaga(1, Vaga.Tipo.CARRO);
        Ticket t = new Ticket(1, new Carro("TST0007"), vaga, LocalDateTime.now());
        assertTrue(t.isAberto());
        assertNull(t.getSaida());
    }

    @Test
    void ticketFechadoAposFechar() {
        Vaga vaga = new Vaga(1, Vaga.Tipo.CARRO);
        Ticket t = new Ticket(1, new Carro("TST0008"), vaga, LocalDateTime.now());
        t.fechar(LocalDateTime.now(), 10.0);
        assertFalse(t.isAberto());
        assertEquals(10.0, t.getValor());
    }

    // ---- Placa ----

    @Test
    void placaNormalizadaParaMaiusculo() {
        Veiculo v = new Carro("abc1234");
        assertEquals("ABC1234", v.getPlaca());
    }
}
