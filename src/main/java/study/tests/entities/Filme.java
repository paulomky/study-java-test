package study.tests.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Filme {
    private String nome;
    private Long estoque;
    private Double precoLocacao;

    @Override
    public String toString() {
        return "<".concat(nome)
                .concat(",")
                .concat(estoque.toString())
                .concat(",")
                .concat(precoLocacao.toString())
                .concat(">")
                ;
    }
}
