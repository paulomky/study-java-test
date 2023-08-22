package study.tests.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.tests.entities.Usuario;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioBuilder {
    private Usuario usuario;
    public static UsuarioBuilder criarUsuario(){
        var builder = new UsuarioBuilder();
        builder.setUsuario(new Usuario("Paulo"));
        return builder;
    }

    public UsuarioBuilder nome(String nome){
        this.getUsuario().setNome(nome);
        return this;
    }

    public Usuario build(){
        return this.getUsuario();
    }
}
