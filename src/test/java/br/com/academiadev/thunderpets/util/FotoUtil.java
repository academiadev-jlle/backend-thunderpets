package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@Component
public class FotoUtil {

    @Autowired
    PetUtil petUtil;

    public List<byte[]> criaTresFotos(){
        List<byte[]> fotos = new ArrayList<>();

        fotos.add(new byte[] {1, 2, 3});

        fotos.add(new byte[] {4, 5, 6});

        fotos.add(new byte[] {7, 8, 9});

        return fotos;
    }

}
