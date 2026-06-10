# Estacionamento

Sistema web de controle de estacionamento — Trabalho 2, Engenharia de Software.

**Stack:** Java 11 · Javalin · SQLite · Docker

---

## Executar com Docker (recomendado)

```bash
docker compose up --build
```

Acesse: [http://localhost:7000](http://localhost:7000)

---

## Executar localmente

Pré-requisitos: Java 11+, Maven 3.8+

```bash
mvn package -DskipTests
java -jar target/estacionamento-1.0-SNAPSHOT.jar
```

---

## Testes

```bash
mvn test
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `PORT` | `7000` | Porta HTTP |
| `DB_PATH` | `estacionamento.db` | Caminho do arquivo SQLite |

---

## Regras de negócio

- Tarifa: R$ 5,00/hora para carros, R$ 3,00/hora para motos
- Cobrança por hora cheia ou fração
- Placa como identificador único por ticket aberto
- 10 vagas para carros (1–10), 10 para motos (11–20)
