package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.contrat.HistoriqueContratDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class HistoriqueContratService {

    private final StatutContratRepository statutContratRepository;
    private final ObjectMapper objectMapper;

    public HistoriqueContratService(StatutContratRepository statutContratRepository, ObjectMapper objectMapper) {
        this.statutContratRepository = statutContratRepository;
        this.objectMapper = objectMapper;
    }

    public List<HistoriqueContratDTO> getHistoriqueByEmployeId(Long employeId) {
        List<StatutContrat> historiques = statutContratRepository.findByEmployeIdWithRelations(employeId);
        return historiques.stream()
                .map(this::mapToHistoriqueDTO)
                .collect(Collectors.toList());
    }

    public List<HistoriqueContratDTO> getHistoriqueByContratEmployeId(Long contratEmployeId) {
        List<StatutContrat> historiques = statutContratRepository.findByContratEmployeIdWithRelations(contratEmployeId);
        return historiques.stream()
                .map(this::mapToHistoriqueDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDifferences(Long statutId) throws Exception {
        StatutContrat statut = statutContratRepository.findById(statutId)
                .orElseThrow(() -> new IllegalArgumentException("Statut introuvable"));

        if (statut.getSnapshotJson() == null) {
            return Map.of("message", "Aucun snapshot disponible pour comparaison");
        }

        // Désérialiser le snapshot
        ContratEmploye ancienContrat = objectMapper.readValue(statut.getSnapshotJson(), ContratEmploye.class);
        ContratEmploye nouveauContrat = statut.getContratEmploye();

        return comparerContratsComplet(ancienContrat, nouveauContrat);
    }

    private HistoriqueContratDTO mapToHistoriqueDTO(StatutContrat statut) {
        HistoriqueContratDTO dto = new HistoriqueContratDTO();
        ContratEmploye contrat = statut.getContratEmploye();

        // Informations de base du statut
        dto.setId(statut.getId());
        dto.setContratEmployeId(contrat.getId());
        dto.setTypeModification(statut.getTypeModification() != null ?
                statut.getTypeModification().name() : null);
        dto.setMotif(statut.getMotif());
        dto.setPreuve(statut.getPreuve());
        dto.setDateEffet(statut.getDateEffet());
        dto.setCreatedAt(statut.getCreatedAt());
        dto.setSnapshotJson(statut.getSnapshotJson());
//        dto.setActif(statut.isActif());
//        dto.setInitialisation(statut.isInitialisation());

        // Informations du contrat
        dto.setTypeContrat(contrat.getType_contrat());
        dto.setSalaireBase(contrat.getSalaire_base());
        dto.setSalaireBrut(contrat.getSalaire_brut());
        dto.setDateFinContrat(contrat.getDate_fin());

        if (contrat.getDepartement() != null) {
            dto.setNomDepartement(contrat.getDepartement().getLibelle());
        }

        if (contrat.getPoste() != null) {
            dto.setNomPoste(contrat.getPoste().getLibelle());
        }

        if (contrat.getCategorieEmploye() != null) {
            dto.setNomCategorie(contrat.getCategorieEmploye().getName());
        }

        return dto;
    }

    private Map<String, Object> comparerContratsComplet(ContratEmploye ancien, ContratEmploye nouveau) {
        Map<String, Object> differences = new HashMap<>();

        // Comparer les champs de base
        differences.put("typeContrat", Map.of(
                "ancien", ancien.getType_contrat(),
                "nouveau", nouveau.getType_contrat(),
                "modifie", !Objects.equals(ancien.getType_contrat(), nouveau.getType_contrat())
        ));

        differences.put("salaireBase", Map.of(
                "ancien", ancien.getSalaire_base(),
                "nouveau", nouveau.getSalaire_base(),
                "modifie", !Objects.equals(ancien.getSalaire_base(), nouveau.getSalaire_base())
        ));

        differences.put("salaireBrut", Map.of(
                "ancien", ancien.getSalaire_brut(),
                "nouveau", nouveau.getSalaire_brut(),
                "modifie", !Objects.equals(ancien.getSalaire_brut(), nouveau.getSalaire_brut())
        ));

        differences.put("dateFinContrat", Map.of(
                "ancien", ancien.getDate_fin(),
                "nouveau", nouveau.getDate_fin(),
                "modifie", !Objects.equals(ancien.getDate_fin(), nouveau.getDate_fin())
        ));

        // Comparer les relations
        differences.put("departement", Map.of(
                "ancien", ancien.getDepartement() != null ?
                        Map.of("id", ancien.getDepartement().getId(), "nom", ancien.getDepartement().getLibelle()) : null,
                "nouveau", nouveau.getDepartement() != null ?
                        Map.of("id", nouveau.getDepartement().getId(), "nom", nouveau.getDepartement().getLibelle()) : null,
                "modifie", !Objects.equals(
                        ancien.getDepartement() != null ? ancien.getDepartement().getId() : null,
                        nouveau.getDepartement() != null ? nouveau.getDepartement().getId() : null
                )
        ));

        // Ajouter d'autres comparaisons au besoin...

        return differences;
    }
}

//@Service
//@Transactional
//public class HistoriqueContratService {
//
//    private final StatutContratRepository statutContratRepository;
//    private final ObjectMapper objectMapper;
//
//    public HistoriqueContratService(StatutContratRepository statutContratRepository, ObjectMapper objectMapper) {
//        this.statutContratRepository = statutContratRepository;
//        this.objectMapper = objectMapper;
//    }
//
//    public List<HistoriqueContratDTO> getHistoriqueByEmployeId(Long employeId) {
//        List<StatutContrat> historiques = statutContratRepository.findHistoriqueCompletByEmployeId(employeId);
//        return historiques.stream()
//                .map(this::mapToHistoriqueDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<HistoriqueContratDTO> getHistoriqueByContratEmployeId(Long contratEmployeId) {
//        // UTILISEZ la nouvelle méthode avec JOIN FETCH
//        List<StatutContrat> historiques = statutContratRepository.findByContratEmployeIdWithRelations(contratEmployeId);
//        return historiques.stream()
//                .map(this::mapToHistoriqueDTO)
//                .collect(Collectors.toList());
//    }
//
//    // CORRIGEZ cette méthode pour utiliser JOIN FETCH
//    public Map<String, Object> getDifferences(Long statutId) throws Exception {
//        // Utilisez la méthode avec JOIN FETCH
//        StatutContrat statut = statutContratRepository.findByIdWithRelations(statutId)
//                .orElseThrow(() -> new IllegalArgumentException("Statut introuvable"));
//
//        if (statut.getSnapshotJson() == null) {
//            return Map.of("message", "Aucun snapshot disponible");
//        }
//
//        // Désérialiser le snapshot
//        ContratEmploye ancienContrat = objectMapper.readValue(statut.getSnapshotJson(), ContratEmploye.class);
//        ContratEmploye nouveauContrat = statut.getContratEmploye();
//
//        return comparerContratsComplet(ancienContrat, nouveauContrat);
//    }
//
//    private HistoriqueContratDTO mapToHistoriqueDTO(StatutContrat statut) {
//        HistoriqueContratDTO dto = new HistoriqueContratDTO();
//        ContratEmploye contrat = statut.getContratEmploye();
//
//        // Informations de base du statut
//        dto.setId(statut.getId());
//        dto.setContratEmployeId(contrat.getId());
//        dto.setTypeModification(statut.getTypeModification() != null ?
//                statut.getTypeModification().name() : null);
//        dto.setMotif(statut.getMotif());
//        dto.setPreuve(statut.getPreuve());
//        dto.setDateEffet(statut.getDateEffet());
//        dto.setCreatedAt(statut.getCreatedAt());
//        dto.setSnapshotJson(statut.getSnapshotJson());
//
//        // Informations du contrat (maintenant chargées avec FETCH)
//        dto.setTypeContrat(contrat.getType_contrat());
//        dto.setSalaireBase(contrat.getSalaire_base());
//        dto.setSalaireBrut(contrat.getSalaire_brut());
//        dto.setDateFinContrat(contrat.getDate_fin());
//
//        if (contrat.getDepartement() != null) {
//            dto.setNomDepartement(contrat.getDepartement().getLibelle());
//        }
//
//        if (contrat.getPoste() != null) {
//            dto.setNomPoste(contrat.getPoste().getLibelle());
//        }
//
//        if (contrat.getCategorieEmploye() != null) {
//            dto.setNomCategorie(contrat.getCategorieEmploye().getName());
//        }
//
//        return dto;
//    }
//
//    // NOUVELLE méthode de comparaison complète
//    private Map<String, Object> comparerContratsComplet(ContratEmploye ancien, ContratEmploye nouveau) {
//        Map<String, Object> differences = new HashMap<>();
//
//        // Comparer les champs de base
//        differences.put("typeContrat", Map.of(
//                "ancien", ancien.getType_contrat(),
//                "nouveau", nouveau.getType_contrat(),
//                "modifie", !Objects.equals(ancien.getType_contrat(), nouveau.getType_contrat())
//        ));
//
//        differences.put("salaireBase", Map.of(
//                "ancien", ancien.getSalaire_base(),
//                "nouveau", nouveau.getSalaire_base(),
//                "modifie", !Objects.equals(ancien.getSalaire_base(), nouveau.getSalaire_base())
//        ));
//
//        differences.put("salaireBrut", Map.of(
//                "ancien", ancien.getSalaire_brut(),
//                "nouveau", nouveau.getSalaire_brut(),
//                "modifie", !Objects.equals(ancien.getSalaire_brut(), nouveau.getSalaire_brut())
//        ));
//
//        differences.put("dateFinContrat", Map.of(
//                "ancien", ancien.getDate_fin(),
//                "nouveau", nouveau.getDate_fin(),
//                "modifie", !Objects.equals(ancien.getDate_fin(), nouveau.getDate_fin())
//        ));
//
//        // Comparer les relations
//        differences.put("departement", Map.of(
//                "ancien", ancien.getDepartement() != null ?
//                        Map.of("id", ancien.getDepartement().getId(), "nom", ancien.getDepartement().getLibelle()) : null,
//                "nouveau", nouveau.getDepartement() != null ?
//                        Map.of("id", nouveau.getDepartement().getId(), "nom", nouveau.getDepartement().getLibelle()) : null,
//                "modifie", !Objects.equals(
//                        ancien.getDepartement() != null ? ancien.getDepartement().getId() : null,
//                        nouveau.getDepartement() != null ? nouveau.getDepartement().getId() : null
//                )
//        ));
//
//        differences.put("poste", Map.of(
//                "ancien", ancien.getPoste() != null ?
//                        Map.of("id", ancien.getPoste().getId(), "nom", ancien.getPoste().getLibelle()) : null,
//                "nouveau", nouveau.getPoste() != null ?
//                        Map.of("id", nouveau.getPoste().getId(), "nom", nouveau.getPoste().getLibelle()) : null,
//                "modifie", !Objects.equals(
//                        ancien.getPoste() != null ? ancien.getPoste().getId() : null,
//                        nouveau.getPoste() != null ? nouveau.getPoste().getId() : null
//                )
//        ));
//
//        differences.put("categorieEmploye", Map.of(
//                "ancien", ancien.getCategorieEmploye() != null ?
//                        Map.of("id", ancien.getCategorieEmploye().getId(), "nom", ancien.getCategorieEmploye().getName()) : null,
//                "nouveau", nouveau.getCategorieEmploye() != null ?
//                        Map.of("id", nouveau.getCategorieEmploye().getId(), "nom", nouveau.getCategorieEmploye().getName()) : null,
//                "modifie", !Objects.equals(
//                        ancien.getCategorieEmploye() != null ? ancien.getCategorieEmploye().getId() : null,
//                        nouveau.getCategorieEmploye() != null ? nouveau.getCategorieEmploye().getId() : null
//                )
//        ));
//
//        return differences;
//    }
//}

//@Service
//@Transactional
//public class HistoriqueContratService {
//
//
//    private final StatutContratRepository statutContratRepository;
//
//
//    private final ObjectMapper objectMapper;
//
//    public HistoriqueContratService(StatutContratRepository statutContratRepository, ObjectMapper objectMapper) {
//        this.statutContratRepository = statutContratRepository;
//        this.objectMapper = objectMapper;
//    }
//
//    public List<HistoriqueContratDTO> getHistoriqueByEmployeId(Long employeId) {
//        List<StatutContrat> historiques = statutContratRepository.findHistoriqueCompletByEmployeId(employeId);
//
//        return historiques.stream()
//                .map(this::mapToHistoriqueDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<HistoriqueContratDTO> getHistoriqueByContratEmployeId(Long contratEmployeId) {
//        List<StatutContrat> historiques = statutContratRepository
//                .findByContratEmployeIdOrderByDateEffetDesc(contratEmployeId);
//
//        return historiques.stream()
//                .map(this::mapToHistoriqueDTO)
//                .collect(Collectors.toList());
//    }
//
//    private HistoriqueContratDTO mapToHistoriqueDTO(StatutContrat statut) {
//        HistoriqueContratDTO dto = new HistoriqueContratDTO();
//
//        // Informations de base du statut
//        dto.setId(statut.getId());
//        dto.setContratEmployeId(statut.getContratEmploye().getId());
//        dto.setTypeModification(statut.getTypeModification() != null ?
//                statut.getTypeModification().name() : null);
//        dto.setMotif(statut.getMotif());
//        dto.setPreuve(statut.getPreuve());
//        dto.setDateEffet(statut.getDateEffet());
//        dto.setCreatedAt(statut.getCreatedAt());
//        dto.setSnapshotJson(statut.getSnapshotJson());
//
//        // Informations du contrat actuel (pour comparaison)
//        ContratEmploye contrat = statut.getContratEmploye();
//        if (contrat != null) {
//            dto.setTypeContrat(contrat.getType_contrat());
//            dto.setSalaireBase(contrat.getSalaire_base());
//            dto.setSalaireBrut(contrat.getSalaire_brut());
//            dto.setDateFinContrat(contrat.getDate_fin());
//
//            if (contrat.getDepartement() != null) {
//                dto.setNomDepartement(contrat.getDepartement().getLibelle());
//            }
//
//            if (contrat.getPoste() != null) {
//                dto.setNomPoste(contrat.getPoste().getLibelle());
//            }
//
//            if (contrat.getCategorieEmploye() != null) {
//                dto.setNomCategorie(contrat.getCategorieEmploye().getName());
//            }
//        }
//
//        return dto;
//    }
//
//    // Méthode pour récupérer les différences entre deux statuts
//    public Map<String, Object> getDifferences(Long statutId) throws Exception {
//        StatutContrat statut = statutContratRepository.findById(statutId)
//                .orElseThrow(() -> new IllegalArgumentException("Statut introuvable"));
//
//        if (statut.getSnapshotJson() == null) {
//            return new HashMap<>();
//        }
//
//        // Désérialiser le snapshot
//        ContratEmploye ancienContrat = objectMapper.readValue(statut.getSnapshotJson(), ContratEmploye.class);
//        ContratEmploye nouveauContrat = statut.getContratEmploye();
//
//        Map<String, Object> differences = new HashMap<>();
//
//        // Comparer les champs
//        if (!Objects.equals(ancienContrat.getType_contrat(), nouveauContrat.getType_contrat())) {
//            differences.put("typeContrat", Map.of(
//                    "ancien", ancienContrat.getType_contrat(),
//                    "nouveau", nouveauContrat.getType_contrat()
//            ));
//        }
//
//        if (!Objects.equals(ancienContrat.getSalaire_base(), nouveauContrat.getSalaire_base())) {
//            differences.put("salaireBase", Map.of(
//                    "ancien", ancienContrat.getSalaire_base(),
//                    "nouveau", nouveauContrat.getSalaire_base()
//            ));
//        }
//
//        // Ajouter d'autres comparaisons selon vos besoins
//
//        return differences;
//    }
//}