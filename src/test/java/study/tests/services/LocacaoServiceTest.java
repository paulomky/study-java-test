package study.tests.services;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import study.tests.builder.FilmeBuilder;
import study.tests.builder.LocacaoBuilder;
import study.tests.builder.UsuarioBuilder;
import study.tests.entities.Filme;
import study.tests.entities.Usuario;
import study.tests.exceptions.CampoObrigatorioException;
import study.tests.exceptions.FilmeSemEstoqueException;
import study.tests.exceptions.UsuarioNegativadoException;
import study.tests.matchers.CustomMatchers;
import study.tests.repository.LocacaoRepository;
import study.tests.utils.DateUtils;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocacaoServiceTest {
    private LocacaoService locacaoService;
    private SPCService spcService;
    private EmailService emailService;
    private LocacaoRepository locacaoRepository;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private Usuario usuario;

    @Before
    public void before(){
        locacaoService = new LocacaoService();

        locacaoRepository = Mockito.mock(LocacaoRepository.class); // <- Muito feio
        spcService = Mockito.mock(SPCService.class); // <- Muito feio

        locacaoService.setLocacaoRepository(locacaoRepository); // <- Muito feio
        locacaoService.setSpcService(spcService); // <- Muito feio

        emailService = Mockito.mock(EmailService.class); // <- Muito feio
        locacaoService.setEmailService(emailService); // <- Muito feio

        usuario = UsuarioBuilder.criarUsuario().build();
    }

    @Test
    public void deveAlugarUmFilmeComSucesso() throws Exception {
        var filmeList = List.of(
                FilmeBuilder.criarFilme()
                        .precoLocacao(18.90D)
                        .build()
        );

        var locacao = locacaoService.alugarFilme(usuario, filmeList);

        error.checkThat(18.90D, CoreMatchers.is(locacao.getValor()));
        error.checkThat(1, CoreMatchers.is(locacao.getFilmes().size()));

        error.checkThat(locacao.getDtLocacao(), CustomMatchers.isToday());
        error.checkThat(locacao.getDtRetorno(), CustomMatchers.todayPlusDays(1L));
    }

    @Test
    public void deveAlugarDoisFilmesComSucesso(){
        var filmeOppenheimer = FilmeBuilder.criarFilme().build();
        var filmeBarbie = FilmeBuilder.criarFilme().build();

        var filmeList = new ArrayList<Filme>();
        filmeList.add(filmeOppenheimer);
        filmeList.add(filmeBarbie);

        var locacao = locacaoService.alugarFilme(usuario, filmeList);
        var valorLocacao = filmeOppenheimer.getPrecoLocacao() + filmeBarbie.getPrecoLocacao();

        error.checkThat(valorLocacao, CoreMatchers.is(locacao.getValor()));
        error.checkThat(2, CoreMatchers.is(locacao.getFilmes().size()));

        error.checkThat(DateUtils.isMesmaData(locacao.getDtLocacao(), OffsetDateTime.now()), CoreMatchers.is(true));
        error.checkThat(DateUtils.isMesmaData(locacao.getDtRetorno(), DateUtils.obterDataComDiferencaDias(1L)), CoreMatchers.is(true));

        error.checkThat(locacao.getFilmes().get(0).equals(filmeOppenheimer), CoreMatchers.is(true));
        error.checkThat(locacao.getFilmes().get(1).equals(filmeBarbie), CoreMatchers.is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() {
        var filmeList = List.of(
                FilmeBuilder.criarFilmeSemEstoque().build()
        );

        locacaoService.alugarFilme(usuario, filmeList);
    }

    @Test
    public void naoDeveAlugarComUsuarioNull() throws CampoObrigatorioException {
        var filmeList = List.of(
                FilmeBuilder.criarFilme().build()
        );

        try {
            locacaoService.alugarFilme(null, filmeList);
            Assert.fail();
        } catch (CampoObrigatorioException e) {
            Assert.assertEquals(e.getMessage(), "O Usuario selecionado para locação não pode ser null.");
        }
    }

    @Test
    public void naoDeveAlugarComFilmeNull() throws CampoObrigatorioException {
        exception.expect(CampoObrigatorioException.class);
        exception.expectMessage("Os Filmes selecionados para locação não podem ser null.");

        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void naoDeveAlugarComListaDeFilmesVazia() throws CampoObrigatorioException {
        var filmeList = new ArrayList<Filme>();

        exception.expect(CampoObrigatorioException.class);
        exception.expectMessage("A lista de Filmes para locação não pode ser vazia.");

        locacaoService.alugarFilme(usuario, filmeList);
    }

    @Test
    public void deveDevolverLocacaoNaSegundaAoAlugarNoSabado(){
        var filmeList = List.of(
                FilmeBuilder.criarFilme().build()
        );

        var locacao = locacaoService.alugarFilme(usuario, filmeList);

        Assert.assertThat(locacao.getDtRetorno(), CustomMatchers.isDayOfWeek(DayOfWeek.MONDAY));
    }

    @Test
    public void naoDeveAlugatFilmeParaUsuarioNegativadoSPC(){
        var filmes = Collections.singletonList(FilmeBuilder.criarFilme().build());

        Mockito.when(spcService.possuiNegativado(Mockito.any(Usuario.class))).thenReturn(true);
        var exceptionMessage = String.format("Não é possível realizar a locação pois o usuário %s está negativado no SPC.", usuario.getNome());

        try {
            locacaoService.alugarFilme(usuario, filmes);
            Assert.fail();
        }
        catch (UsuarioNegativadoException e){
            Assert.assertThat(e.getMessage(), CoreMatchers.is(exceptionMessage));
        }

        Mockito.verify(spcService).possuiNegativado(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        var usuario2 = UsuarioBuilder.criarUsuario().nome("Jorge").build();
        var filmes2 = Arrays.asList(
                FilmeBuilder.criarFilme().nome("Barbie").build()
                , FilmeBuilder.criarFilme().build()
        );

        var usuario3 = UsuarioBuilder.criarUsuario().nome("Augusto").build();
        var filmes3 = Collections.singletonList(FilmeBuilder.criarFilme().nome("Talk to Me").build());

        var locacao = Arrays.asList(
                LocacaoBuilder.criarLocacao()
                        .usuario(usuario)
                        .atrasado()
                        .build()
                , LocacaoBuilder.criarLocacao()
                        .usuario(usuario2)
                        .filmes(filmes2)
                        .atrasado()
                        .build()
                , LocacaoBuilder.criarLocacao()
                        .usuario(usuario3)
                        .filmes(filmes3)
                        .build()
        );

        Mockito.when(locacaoRepository.getLocacoesComDevolucoesAtrasadas()).thenReturn(locacao);

        locacaoService.notificarDevolucoesAtrasadas();

        Mockito.verify(emailService).notificarEmail(locacao.get(0));
        Mockito.verify(emailService).notificarEmail(locacao.get(1));
        Mockito.verify(emailService, Mockito.never()).notificarEmail(locacao.get(2));
        Mockito.verifyNoMoreInteractions(emailService);
    }
}
