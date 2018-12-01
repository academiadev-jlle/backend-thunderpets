package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.service.FacebookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FacebookServiceImpl implements FacebookService {

    @Value("${spring.social.facebook.appId}")
    private String appId;

    @Value("${spring.social.facebook.appSecret}")
    private String appSecret;

    @Value("${spring.social.facebook.redirectUri}")
    private String redirectUri;

    protected String token;

    @Override
    public String criarUrlAutorizacaoFacebook() {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(appId, appSecret);

        OAuth2Operations operacoes = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri(redirectUri);
        params.setScope("public_profile,email");

        return operacoes.buildAuthorizeUrl(params);
    }

    @Override
    public void criarTokenFacebook(String codigo) {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(appId, appSecret);
        AccessGrant grant = connectionFactory.getOAuthOperations().exchangeForAccess(codigo, redirectUri, null);
        token = grant.getAccessToken();
    }

    public Optional<Usuario> getUsuarioFacebook() {
        Facebook facebook = new FacebookTemplate(token);

        String[] campos = {
                "id",
                "first_name",
                "last_name",
                "middle_name",
                "name",
                "name_format",
                "picture",
                "short_name",
                "email",
        };

        return Optional.ofNullable();
    }
}
