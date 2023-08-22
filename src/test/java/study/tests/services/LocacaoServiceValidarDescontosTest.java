package study.tests.services;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import study.tests.builder.FilmeBuilder;
import study.tests.builder.UsuarioBuilder;
import study.tests.entities.Filme;
import study.tests.entities.Usuario;
import study.tests.repository.LocacaoRepository;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class LocacaoServiceValidarDescontosTest {
    @InjectMocks
    private LocacaoService locacaoService;
    @Mock
    private SPCService spcService;
    @Mock
    private LocacaoRepository locacaoRepository;
    private final Usuario usuario = UsuarioBuilder.criarUsuario().build();

    @Parameter
    public List<Filme> filmeList;

    @Parameter(value = 1)
    public Double valorLocacao;

    @Parameter(value = 2)
    public String nameParam;

    private static final Filme filmeBarvie = FilmeBuilder.criarFilme().precoLocacao(15.00D).build();
    private static final Filme filmeEsquadraoSuicida = FilmeBuilder.criarFilme().precoLocacao(10.00D).build();
    private static final Filme filmeOppenheimer =  FilmeBuilder.criarFilme().precoLocacao(25.00D).build();
    private static final Filme filmeTropaDeElite = FilmeBuilder.criarFilme().precoLocacao(10.00D).build();
    private static final Filme filmeProcurandoNemo = FilmeBuilder.criarFilme().precoLocacao(15.00D).build();
    private static final Filme filmeInterstellar = FilmeBuilder.criarFilme().precoLocacao(25.00D).build();
    private static final Filme filmePerdidoEmMarte = FilmeBuilder.criarFilme().precoLocacao(10.00D).build();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameters(name = "{2}")
    public static Collection<Object[]> getParam(){
        return List.of(new Object[][]{
                {List.of(filmeBarvie, filmeEsquadraoSuicida), 25.00D, "2 Filmes: Sem Desconto"}
                , {List.of(filmeBarvie, filmeEsquadraoSuicida, filmeOppenheimer), 43.75D, "3 Filmes: 25% de Desconto"}
                , {List.of(filmeBarvie, filmeEsquadraoSuicida, filmeOppenheimer, filmeTropaDeElite), 48.75D, "4 Filme: 50% de Desconto"}
                , {List.of(filmeBarvie, filmeEsquadraoSuicida, filmeOppenheimer, filmeTropaDeElite, filmeProcurandoNemo), 52.5D, "5 Filmes: 75% de Desconto"}
                , {List.of(filmeBarvie, filmeEsquadraoSuicida, filmeOppenheimer, filmeTropaDeElite, filmeProcurandoNemo, filmeInterstellar), 52.5D, "6 Filmes: 100% de Desconto"}
                , {List.of(filmeBarvie, filmeEsquadraoSuicida, filmeOppenheimer, filmeTropaDeElite, filmeProcurandoNemo, filmeInterstellar, filmePerdidoEmMarte), 62.5D, "7 Filmes: Sem Desconto"}
        });
    }

    @Test
    public void deveValidarDesconto25NoTerceiroFilmeAlugado() {
        var locacao = locacaoService.alugarFilme(usuario, filmeList);
        Assert.assertThat(valorLocacao, CoreMatchers.is(locacao.getValor()));
    }
}
