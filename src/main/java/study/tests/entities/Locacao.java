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

    @Override
    public String toString() {
        return "<".concat(usuario.toString())
                .concat(",")
                .concat(filmes.toString())
                .concat(",")
                .concat(dtLocacao.toString())
                .concat(",")
                .concat(dtRetorno.toString())
                .concat(",")
                .concat(valor.toString())
                .concat(">")
                ;
    }
}
