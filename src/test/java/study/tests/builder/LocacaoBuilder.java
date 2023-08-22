package study.tests.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.tests.entities.Filme;
import study.tests.entities.Locacao;
import study.tests.entities.Usuario;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocacaoBuilder {
    private Locacao locacao;
    public static LocacaoBuilder criarLocacao() {
        var locacaoBuilder = new LocacaoBuilder();

        var filmes = Collections.singletonList(FilmeBuilder.criarFilme().build());
        var usuario = UsuarioBuilder.criarUsuario().build();
        var locacao = new Locacao();

        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDtLocacao(OffsetDateTime.now());
        locacao.setDtRetorno(OffsetDateTime.now().plusDays(2L));
        locacao.setValor(25D);

        locacaoBuilder.setLocacao(locacao);

        return locacaoBuilder;
    }

    public LocacaoBuilder valor(Double valor){
        this.getLocacao().setValor(valor);
        return this;
    }

    public LocacaoBuilder usuario(Usuario usuario){
        this.getLocacao().setUsuario(usuario);
        return this;
    }

    public LocacaoBuilder dtRetorno(OffsetDateTime dtRetorno){
        this.getLocacao().setDtRetorno(dtRetorno);
        return this;
    }

    public LocacaoBuilder filmes(List<Filme> filmes){
        this.getLocacao().setFilmes(filmes);
        return this;
    }

    public LocacaoBuilder atrasado(){
        this.getLocacao().setDtLocacao(OffsetDateTime.now().minusDays(4));
        this.getLocacao().setDtRetorno(OffsetDateTime.now().minusDays(2));
        return this;
    }

    public Locacao build(){
        return this.getLocacao();
    }
}