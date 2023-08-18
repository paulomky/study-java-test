package study.tests.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class Locacao {
    private Usuario usuario;
    private List<Filme> filmes;
    private OffsetDateTime dtLocacao;
    private OffsetDateTime dtRetorno;
    private Double valor;
}
