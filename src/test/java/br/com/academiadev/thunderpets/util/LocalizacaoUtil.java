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

    public Localizacao criaLocalizacaoTerminalCentral() {
        Localizacao localizacao = new Localizacao();
        localizacao.setCidade("Joinville");
        localizacao.setEstado("Santa Catarina");
        localizacao.setDescricao("R. Dona Francisca - Centro, Joinville - SC, 89201-070");
        localizacao.setLatitude(new BigDecimal(-26.301245));
        localizacao.setLongitude(new BigDecimal(-48.844594));

        return localizacao;
    }

    public Localizacao criaLocalizacaoAmerica() {
        Localizacao localizacao = new Localizacao();
        localizacao.setCidade("Joinville");
        localizacao.setEstado("Blumenal");
        localizacao.setDescricao("R. Carlos Gruensch, 2-260 - América, Joinville - SC, 89201-745");
        localizacao.setLatitude(new BigDecimal(-26.300709));
        localizacao.setLongitude(new BigDecimal(-48.857203));

        return localizacao;
    }

}
