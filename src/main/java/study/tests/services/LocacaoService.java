package study.tests.services;

import study.tests.entities.Filme;
import study.tests.entities.Locacao;
import study.tests.entities.Usuario;
import study.tests.exceptions.CampoObrigatorioException;
import study.tests.exceptions.FilmeSemEstoqueException;
import study.tests.repository.LocacaoRepository;
import study.tests.utils.DateUtils;

import java.time.OffsetDateTime;
import java.util.List;

public class LocacaoService {
    private LocacaoRepository locacaoRepository;
    public Locacao alugarFilme(Usuario usuario, List<Filme> filmeList) {

        validFilme(filmeList);
        validUsuario(usuario);

        var now = OffsetDateTime.now();

        var locacao = new Locacao();
        locacao.setUsuario(usuario);
        locacao.setFilmes(filmeList);
        locacao.setDtLocacao(now);

        var index = 1;
        var precoLocacao = 0D;
        for(var filme : filmeList) {

            var valorFilme = filme.getPrecoLocacao();

            valorFilme = switch (index) {
                case 3 -> valorFilme - (valorFilme * 0.25D);
                case 4 -> valorFilme - (valorFilme * 0.50D);
                case 5 -> valorFilme - (valorFilme * 0.75D);
                case 6 -> 0D;
                default -> filme.getPrecoLocacao();
            };

            precoLocacao = precoLocacao + valorFilme;
            index++;
        }

        locacao.setValor(precoLocacao);

        var dataEntrega = DateUtils.adicionarDias(now, 1L);
        locacao.setDtRetorno(dataEntrega);

        //TODO salvar no banco
        locacaoRepository.salvar(locacao);

        return locacao;
    }

    private void validUsuario(Usuario usuario) {
        if(usuario == null){
            throw new CampoObrigatorioException("O Usuario selecionado para locação não pode ser null.");
        }
    }

    public void validFilme(List<Filme> filmeList) {
        if(filmeList == null){
            throw new CampoObrigatorioException("Os Filmes selecionados para locação não podem ser null.");
        }

        if(filmeList.isEmpty()){
            throw new CampoObrigatorioException("A lista de Filmes para locação não pode ser vazia.");
        }

        for(var filme : filmeList) {
            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException(filme);
            }
        }
    }
}
