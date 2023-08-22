package study.tests.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario {
    private String nome;

    @Override
    public String toString() {
        return "<".concat(nome).concat(">");
    }
}
