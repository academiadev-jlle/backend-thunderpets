package br.com.academiadev.thunderpets.service.impl;

import br.com.academiadev.thunderpets.service.GoogleService;
import org.springframework.beans.factory.annotation.Value;
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


@Service
public class GoogleServiceImpl implements GoogleService {

    @Value("${spring.social.google.appId}")
    private String googleAppId;

    @Value("${spring.social.google.appSecret}")
    private String googleAppSecret;

    @Value("${spring.social.google.redirectUri}")
    private String redirectUri;

    @Override
    public String criarUrlAutorizacaoGoogle() {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleAppSecret);

        OAuth2Operations operacoes = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri(redirectUri);
        params.setScope("profile email");

        return operacoes.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
    }

    public void login(String code) {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleAppSecret);
        AccessGrant grant = connectionFactory.getOAuthOperations().exchangeForAccess(code, redirectUri, null);

        String token = grant.getAccessToken();

        Google google = new GoogleTemplate(token);
        GoogleUserInfo userInfo = google.userOperations().getUserInfo();

        userInfo.getEmail();
        userInfo.getName();
    }
}
