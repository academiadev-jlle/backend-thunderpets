package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Localizacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LocalizacaoUtil {

    public Localizacao criaLocalizacaoGaruva() {
        return Localizacao.builder()
                .cidade("Garuva")
                .estado("Garuva State")
                .latitude(new BigDecimal("-26.294918"))
                .longitude(new BigDecimal("-48.890790"))
                .build();
    }

    public Localizacao criaLocalizacaoTerminalCentral() {
        return Localizacao.builder()
                .cidade("Joinville")
                .estado("Santa Catarina")
                .descricao("R. Dona Francisca - Centro, Joinville - SC, 89201-070")
                .latitude(new BigDecimal("-26.301245"))
                .longitude(new BigDecimal("-48.844594"))
                .build();
    }

    public Localizacao criaLocalizacaoAmerica() {
        return Localizacao.builder()
                .cidade("Joinville")
                .estado("Blumenal")
                .descricao("R. Carlos Gruensch, 2-260 - América, Joinville - SC, 89201-745")
                .latitude(new BigDecimal("-26.300709"))
                .longitude(new BigDecimal("-48.857203"))
                .build();
    }

}
