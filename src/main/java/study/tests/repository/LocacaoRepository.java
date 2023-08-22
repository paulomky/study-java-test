package study.tests.repository;

import study.tests.entities.Locacao;

import java.util.List;

public interface LocacaoRepository {
    Locacao salvar(Locacao locacao);
    List<Locacao> getLocacoesComDevolucoesAtrasadas();
}
