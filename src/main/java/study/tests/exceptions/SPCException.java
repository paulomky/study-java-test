package study.tests.exceptions;

public class SPCException extends RuntimeException {
    public SPCException(String error){
        super(String.format("Erro ao consumir servico SPC: %s", error));
    }
}
