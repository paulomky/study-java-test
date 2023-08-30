package study.tests.services;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import study.tests.builder.FilmeBuilder;
import study.tests.builder.LocacaoBuilder;
import study.tests.builder.UsuarioBuilder;
import study.tests.entities.Filme;
import study.tests.entities.Locacao;
import study.tests.entities.Usuario;
import study.tests.exceptions.CampoObrigatorioException;
import study.tests.exceptions.FilmeSemEstoqueException;
import study.tests.exceptions.SPCException;
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
    @InjectMocks @Spy
    private LocacaoService locacaoService;
    @Mock
    private SPCService spcService;
    @Mock
    private EmailService emailService;
    @Mock
    private LocacaoRepository locacaoRepository;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private final Usuario usuario = UsuarioBuilder.criarUsuario().build();;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deveAlugarUmFilmeComSucesso() throws Exception {
        var filmeList = List.of(
                FilmeBuilder.criarFilme()
                        .precoLocacao(18.90D)
                        .build()
        );
        var dateNowMock = OffsetDateTime.parse("2023-08-30T15:00:00.000Z"); //Quarta-feira

        Mockito.doReturn(dateNowMock).when(locacaoService).getNow();

        var locacao = locacaoService.alugarFilme(usuario, filmeList);

        error.checkThat(18.90D, CoreMatchers.is(locacao.getValor()));
        error.checkThat(1, CoreMatchers.is(locacao.getFilmes().size()));

        error.checkThat(locacao.getDtLocacao(), CustomMatchers.sameDate(dateNowMock));
        error.checkThat(locacao.getDtRetorno(), CustomMatchers.sameDate(dateNowMock.plusDays(1L)));
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
            Assert.assertEquals("O Usuario selecionado para locação não pode ser null.", e.getMessage());
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

        Mockito.doReturn(OffsetDateTime.parse("2023-08-26T00:00:00.000Z")).when(locacaoService).getNow(); //Sábado

        var locacao = locacaoService.alugarFilme(usuario, filmeList);

        Assert.assertThat(locacao.getDtRetorno().getDayOfWeek(), CoreMatchers.is(DayOfWeek.MONDAY));
    }

    @Test
    public void naoDeveAlugatFilmeParaUsuarioNegativadoSPC() throws Exception {
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

    @Test
    public void deveTratarErroSPC() throws Exception {
        var filmes = Collections.singletonList(FilmeBuilder.criarFilme().build());

        Mockito.when(spcService.possuiNegativado(usuario)).thenThrow(new Exception("503 Service Unavailable"));

        exception.expect(SPCException.class);
        exception.expectMessage("Erro ao consumir servico SPC: 503 Service Unavailable");

        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao(){
        var locacao = LocacaoBuilder.criarLocacao().build();

        locacaoService.prorrogarLocacao(locacao, 5L);

        var argCapture = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(locacaoRepository).salvar(argCapture.capture());

        var locacaoCapturada = argCapture.getValue();

        error.checkThat(locacaoCapturada.getValor(), CoreMatchers.is(125D));
        error.checkThat(locacaoCapturada.getDtLocacao(), CustomMatchers.isToday());
        error.checkThat(locacaoCapturada.getDtRetorno(), CustomMatchers.todayPlusDays(5L));
    }
}