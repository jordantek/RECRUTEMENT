package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.MotifArretContrat;
import com.tpc.tpcgestpaie.localapp.repository.MotifArretContratRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotifArretContratService {

    private final MotifArretContratRepository repository;

    public MotifArretContratService(MotifArretContratRepository repository) {
        this.repository = repository;
    }

    public List<MotifArretContrat> findAll() {
        return repository.findAll();
    }

    public Optional<MotifArretContrat> findById(Long id) {
        return repository.findById(id);
    }

    public MotifArretContrat save(MotifArretContrat motif) {
        return repository.save(motif);
    }

    public MotifArretContrat update(Long id, MotifArretContrat motifDetails) {
        return repository.findById(id).map(motif -> {
            motif.setLibelle(motifDetails.getLibelle());
            motif.setDescription(motifDetails.getDescription());
            motif.setDeleted_at(motifDetails.getDeleted_at());
            return repository.save(motif);
        }).orElseThrow(() -> new RuntimeException("Motif d’arrêt non trouvé avec id " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // 🔥 Ajout pour le contrôle de doublon
    public boolean existsByLibelle(String libelle) {
        return repository.existsByLibelle(libelle);
    }

    // ✅ méthode deleteById à ajouter
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
