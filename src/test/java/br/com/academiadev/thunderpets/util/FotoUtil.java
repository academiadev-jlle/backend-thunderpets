package br.com.academiadev.thunderpets.util;

import br.com.academiadev.thunderpets.model.Foto;
import br.com.academiadev.thunderpets.model.Pet;
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

    public List<Foto> criaTresFotos(){
        List<Foto> fotos = new ArrayList<>();

        Pet pet = petUtil.criaPetBrabo();

        Foto fotoA = new Foto();
        fotoA.setImage(new byte[]{1,2,3});
        fotoA.setPet(pet);

        Foto fotoB = new Foto();
        fotoB.setImage(new byte[]{4,5,6});
        fotoB.setPet(pet);

        Foto fotoC = new Foto();
        fotoC.setImage(new byte[]{7,8,9});
        fotoC.setPet(pet);

        fotos.add(fotoA);
        fotos.add(fotoB);
        fotos.add(fotoB);

        return fotos;
    }

}
