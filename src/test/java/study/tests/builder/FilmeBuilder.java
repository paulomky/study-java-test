package study.tests.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.tests.entities.Filme;

@Getter
@Setter
@NoArgsConstructor
public class FilmeBuilder {
    private Filme filme;
    public static FilmeBuilder criarFilme(){
        var build = new FilmeBuilder();
        build.setFilme(new Filme("Oppenheimer", 15L, 25.00D));
        return build;
    }

    public static FilmeBuilder criarFilmeSemEstoque(){
        var build = new FilmeBuilder();
        build.setFilme(new Filme("Oppenheimer", 0L, 25.00D));
        return build;
    }

    public FilmeBuilder precoLocacao(Double precoLocacao){
        this.getFilme().setPrecoLocacao(precoLocacao);
        return this;
    }

    public Filme build(){
        return this.getFilme();
    }
}
