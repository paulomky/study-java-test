package study.tests.exceptions;

import study.tests.entities.Usuario;

public class UsuarioNegativadoException extends RuntimeException {
    public UsuarioNegativadoException(Usuario usuario){
        super(String.format("Não é possível realizar a locação pois o usuário %s está negativado no SPC.", usuario.getNome()));
    }
}