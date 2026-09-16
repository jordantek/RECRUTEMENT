package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.MinistereTravail;
import com.tpc.tpcgestpaie.localapp.repository.MinistereTravailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinistereTravailService {
    private final MinistereTravailRepository repository;
    private final MinistereTravailRepository ministereTravailRepository;

    public MinistereTravailService(MinistereTravailRepository repository, MinistereTravailRepository ministereTravailRepository) {
        this.repository = repository;
        this.ministereTravailRepository = ministereTravailRepository;
    }

    public List<MinistereTravail> findAll() {
        return repository.findAll();
    }

    public Optional<MinistereTravail> findById(Long id) {
        return repository.findById(id);
    }

//    public MinistereTravail save(MinistereTravail ministereTravail) {
//        return repository.save(ministereTravail);
//    }

    public MinistereTravail save(MinistereTravail ministereTravail) {
        if (repository.count() > 0) {
            throw new RuntimeException("Un enregistrement existe déjà, vous ne pouvez pas en ajouter un autre !");
        }
        return repository.save(ministereTravail);
    }

//    public MinistereTravail update(Long id, MinistereTravail ministereTravail) {
//        return repository.findById(id).map(existing -> {
//            existing.setMinistere(ministereTravail.getMinistere());
//            existing.setServiceSecuriteSociale(ministereTravail.getServiceSecuriteSociale());
//            existing.setDirectionEnregistrementContrat(ministereTravail.getDirectionEnregistrementContrat());
//            existing.setDirecteurDepartementalTravail(ministereTravail.getDirecteurDepartementalTravail());
//            existing.setAdded_by(ministereTravail.getAdded_by());
//            return repository.save(existing);
//        }).orElseThrow(() -> new RuntimeException("MinistereTravail non trouvé avec id: " + id));
//    }

    public MinistereTravail update(Long id, MinistereTravail newData) {
        return repository.findById(id).map(existing -> {
            existing.setMinistere(newData.getMinistere());
            existing.setServiceSecuriteSociale(newData.getServiceSecuriteSociale());
            existing.setDirectionEnregistrementContrat(newData.getDirectionEnregistrementContrat());
            existing.setDirecteurDepartementalTravail(newData.getDirecteurDepartementalTravail());
            // ⚠️ on NE TOUCHE PAS à existing.setUniqueKey()
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Enregistrement introuvable avec id=" + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public MinistereTravail getUniqueMinistere() {
        return ministereTravailRepository.findTopByOrderByIdAsc();
    }
}
