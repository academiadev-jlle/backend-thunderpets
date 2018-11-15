package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Localizacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
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
