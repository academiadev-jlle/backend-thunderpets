package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Localizacao;

import java.math.BigDecimal;

public class LocalizacaoUtil {

    public Localizacao criaLocalizacaoGaruva() {
        Localizacao localizacao = new Localizacao();
        localizacao.setCidade("Garuva");
        localizacao.setEstado("Garuva State");
        localizacao.setLatitude(new BigDecimal(0));
        localizacao.setLongitude(new BigDecimal(0));

        return localizacao;
    }

}
