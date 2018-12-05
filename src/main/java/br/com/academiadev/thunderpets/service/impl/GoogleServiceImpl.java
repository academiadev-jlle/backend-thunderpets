package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.model.Usuario;
import br.com.academiadev.thunderpets.repository.UsuarioRepository;
import br.com.academiadev.thunderpets.service.GoogleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.HashMap;
import java.util.Optional;

@Service
public class GoogleServiceImpl implements GoogleService {

    @Value("${spring.social.google.appId}")
    private String googleAppId;

    @Value("${spring.social.google.appSecret}")
    private String googleAppSecret;

    @Value("${spring.social.google.redirectUri}")
    private String redirectUri;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    private UsuarioRepository usuarioRepository;
    private final TokenEndpoint tokenEndpoint;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public GoogleServiceImpl(UsuarioRepository usuarioRepository, TokenEndpoint tokenEndpoint) {
        this.usuarioRepository = usuarioRepository;
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public String criarUrlAutorizacaoGoogle() {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleAppSecret);

        OAuth2Operations operacoes = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri(redirectUri);
        params.setScope("profile email");

        return operacoes.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
    }

    public Optional<OAuth2AccessToken> login(String code) {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleAppSecret);
        AccessGrant grant = connectionFactory.getOAuthOperations().exchangeForAccess(code, redirectUri, null);

        String token = grant.getAccessToken();

        if (token.isEmpty()) {
            return Optional.empty();
        }

        return getUsuarioGoogle(token);
    }

    private Optional<OAuth2AccessToken> getUsuarioGoogle(String token) {
        Google google = new GoogleTemplate(token);
        GoogleUserInfo userInfo = google.userOperations().getUserInfo();

        if (userInfo == null) {
            return Optional.empty();
        }

        String access = userInfo.getEmail();
        if (usuarioRepository.findOneByEmail(access) == null) {
            usuarioRepository.saveAndFlush(Usuario.builder()
                    .email(access)
                    .nome(userInfo.getName())
                    .senha(passwordEncoder.encode(userInfo.getId()))
                    .ativo(true).build());
        }

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(clientId, null, null);
        SecurityContextHolder.getContext().setAuthentication(principal);

        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("username", access);
        params.put("password", userInfo.getId());

        try {
            return Optional.ofNullable(tokenEndpoint.getAccessToken(principal, params).getBody());
        } catch (HttpRequestMethodNotSupportedException e) {
            return Optional.empty();
        }
    }
}
