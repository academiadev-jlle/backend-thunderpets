package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.dto.LoginSocialDTO;
import br.com.academiadev.thunderpets.exception.UsuarioNaoEncontradoException;
import br.com.academiadev.thunderpets.model.LoginSocial;
import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.LoginSocialRepository;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.service.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

@Service
public class FacebookServiceImpl implements FacebookService {

    @Value("${spring.social.facebook.appId}")
    private String facebookAppId;

    @Value("${spring.social.facebook.appSecret}")
    private String facebookAppSecret;

    @Value("${spring.social.facebook.redirectUri}")
    private String redirectUri;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${spring.social.senha-provisoria}")
    private String senhaProvisoria;

    private UsuarioRepository usuarioRepository;
    private LoginSocialRepository loginSocialRepository;
    private final TokenEndpoint tokenEndpoint;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public FacebookServiceImpl(UsuarioRepository usuarioRepository, LoginSocialRepository loginSocialRepository, TokenEndpoint tokenEndpoint) {
        this.usuarioRepository = usuarioRepository;
        this.loginSocialRepository = loginSocialRepository;
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public String criarUrlAutorizacaoFacebook() {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookAppSecret);

        OAuth2Operations operacoes = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri(redirectUri);
        params.setScope("public_profile,email");

        return operacoes.buildAuthorizeUrl(params);
    }

    @Override
    public Optional<OAuth2AccessToken> login(LoginSocialDTO dto) {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookAppSecret);
        AccessGrant grant = connectionFactory.getOAuthOperations().exchangeForAccess(dto.getCode(), dto.getRedirectUri(), null);

        String token = grant.getAccessToken();

        if (token.isEmpty()) {
            return Optional.empty();
        }

        return getUsuarioFacebook(token);
    }

    private Optional<OAuth2AccessToken> getUsuarioFacebook(String token) {
        Facebook facebook = new FacebookTemplate(token);

        String[] campos = {
                "id",
                "name",
                "picture",
                "short_name",
                "email"
        };

        User usuarioFacebook = Optional.ofNullable(facebook.fetchObject("me", User.class, campos)).orElse(null);

        if (usuarioFacebook == null) {
            return Optional.empty();
        }

        Usuario usuario;
        Optional<LoginSocial> usuarioSocial = loginSocialRepository.findByIdSocial(usuarioFacebook.getId());

        if (usuarioSocial.isPresent()) {
            usuario = usuarioRepository.findById(usuarioSocial.get().getId()).get();
        } else {
            String access = usuarioFacebook.getEmail() == null ? usuarioFacebook.getId() : usuarioFacebook.getEmail();

            usuario = usuarioRepository.findOneByEmail(access);

            if (usuario == null) {
                usuario = usuarioRepository.saveAndFlush(Usuario.builder()
                        .email(access)
                        .nome(usuarioFacebook.getName())
                        .senha(passwordEncoder.encode(senhaProvisoria))
                        .foto(facebook.userOperations().getUserProfileImage())
                        .ativo(true).build());

                LoginSocial loginSocial = new LoginSocial();
                loginSocial.setIdSocial(usuarioFacebook.getId());
                loginSocial.setUsuario(usuario);

                loginSocialRepository.saveAndFlush(loginSocial);
            }
        }

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, null, null);
        SecurityContextHolder.getContext().setAuthentication(principal);

        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("username", usuario.getEmail());
        params.put("password", senhaProvisoria);

        try {
            return Optional.ofNullable(tokenEndpoint.getAccessToken(principal, params).getBody());
        } catch (HttpRequestMethodNotSupportedException e) {
            return Optional.empty();
        } catch (InvalidGrantException e) {
            throw new UsuarioNaoEncontradoException("Este endereço de e-mail já está cadastrado.");
        }
    }

    private Optional<byte[]> getFoto(User userInfo) {
        try {
            URL photoUrl = new URL(userInfo.getCover().getSource());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream inputStream = photoUrl.openStream();
            byte[] buffer = new byte[1024];
            int n = 0;

            while ((n = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }

            return Optional.ofNullable(baos.toByteArray());
        } catch (MalformedURLException e) {
            System.out.println("Erro na URL");
        } catch (IOException e) {
            System.out.println("Erro na leitura do InputStream");
        }

        return Optional.empty();
    }
}
