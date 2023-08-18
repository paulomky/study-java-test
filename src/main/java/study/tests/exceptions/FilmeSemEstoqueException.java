package study.tests.exceptions;

import study.tests.entities.Filme;

public class FilmeSemEstoqueException extends RuntimeException {
    public FilmeSemEstoqueException(Filme filme){
        super(String.format("Não é possível realizar a locação do filme %s pois o mesmo se encontra sem estoque no momento.", filme.getNome()));
    }
}
