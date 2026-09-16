    package com.tpc.tpcgestpaie.localapp.service;

    import com.tpc.tpcgestpaie.localapp.model.Poste;
    import com.tpc.tpcgestpaie.localapp.model.Departement;
    import com.tpc.tpcgestpaie.localapp.repository.PosteRepository;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class PosteService {

        private final PosteRepository posteRepository;

        public PosteService(PosteRepository posteRepository) {
            this.posteRepository = posteRepository;
        }

        // Créer un nouveau poste
        public Poste createPoste(Poste poste) {
            return posteRepository.save(poste);
        }

        // Mettre à jour un poste
        public Poste updatePoste(Long posteId, Poste posteDetails) {
            Optional<Poste> poste = posteRepository.findById(posteId);
            if (poste.isPresent()) {
                Poste existingPoste = poste.get();
                existingPoste.setLibelle(posteDetails.getLibelle());
                existingPoste.setDescription(posteDetails.getDescription());
                existingPoste.setDepartement(posteDetails.getDepartement());
                existingPoste.setAdded_by(posteDetails.getAdded_by());
                return posteRepository.save(existingPoste);
            }
            return null;
        }

        // Supprimer un poste
        public boolean deletePoste(Long posteId) {
            Optional<Poste> poste = posteRepository.findById(posteId);
            if (poste.isPresent()) {
                posteRepository.delete(poste.get());
                return true;
            }
            return false;
        }

        // Trouver un poste par ID
        public Optional<Poste> getPosteById(Long posteId) {
            return posteRepository.findById(posteId);
        }

        // Trouver les postes par département
        public List<Poste> getPostesByDepartement(Departement departement) {
            return posteRepository.findByDepartement(departement);
        }

        // Trouver un poste par libellé
        public Optional<Poste> getPosteByLibelle(String libelle) {
            return posteRepository.findByLibelle(libelle);
        }

        public List<Poste> getAllPostes() {
            return posteRepository.findAll();
        }


        public boolean existsByLibelleAndDepartementId(String libelle, Long departementId) {
            return posteRepository.existsByLibelleAndDepartement_Id(libelle, departementId);
        }

    }
