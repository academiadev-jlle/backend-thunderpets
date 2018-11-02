package br.com.academiadev.thunderpets.dto;

import br.com.academiadev.thunderpets.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UsuarioCustomDTO implements UserDetails {

    private String email;
    private String senha;
    private boolean ativo;

    public UsuarioCustomDTO(Usuario byEmail) {
        this.email = byEmail.getEmail();
        this.senha = byEmail.getSenha();
        this.ativo = byEmail.isAtivo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return ativo;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ativo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return ativo;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
