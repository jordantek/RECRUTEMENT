package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratConsolideDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ModificationContrat;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContratConsolidationService {

    private static final Logger log = LoggerFactory.getLogger(ContratConsolidationService.class);

    private final StatutContratRepository statutContratRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final ObjectMapper objectMapper;

    public ContratConsolidationService(StatutContratRepository statutContratRepository,
                                       ContratEmployeRepository contratEmployeRepository,
                                       ObjectMapper objectMapper) {
        this.statutContratRepository = statutContratRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.objectMapper = objectMapper;

        // Configuration de l'ObjectMapper pour supporter Java 8 dates et éviter les erreurs Hibernate
        configureObjectMapper();
    }

    /**
     * Configure l'ObjectMapper pour supporter les types Java 8 date/time et gérer Hibernate
     */
    private void configureObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Crée un ObjectMapper configuré pour les dates Java 8 et Hibernate
     */
    private ObjectMapper createConfiguredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    public ContratConsolideDTO getContratConsolide(Long employeId, LocalDate dateReference) throws Exception {
        log.info("🔍 DEBUT - Consolidation contrat pour employé {} jusqu'à {}", employeId, dateReference);

        try {
            // 1. Trouver le contrat initial
            StatutContrat contratInitialStatut = statutContratRepository.findStatutInitialisationByEmployeId(employeId)
                    .orElseThrow(() -> new IllegalArgumentException("Aucun contrat initial trouvé pour l'employé ID: " + employeId));

            log.info("✅ Statut initial trouvé - ID: {}, Date: {}",
                    contratInitialStatut.getId(), contratInitialStatut.getDateEffet());

            // 2. DEBUG: Récupérer tous les statuts de l'employé
            List<StatutContrat> tousStatuts = statutContratRepository.findByEmployeIdOrderByDateEffetDesc(employeId);
            log.info("📊 TOTAL statuts pour l'employé {}: {}", employeId, tousStatuts.size());

            for (StatutContrat statut : tousStatuts) {
                log.info("   - Statut ID: {}, Initialisation: {}, Date: {}, Type: {}",
                        statut.getId(), statut.isInitialisation(), statut.getDateEffet(),
                        statut.getTypeModification());
            }

            // 3. Essayer d'abord la méthode simple
            List<StatutContrat> modifications = statutContratRepository.findModificationsSimple(employeId, dateReference);
            log.info("📊 Modifications trouvées (méthode simple): {}", modifications.size());

            // 4. Si aucune modification, essayer le filtrage manuel
            if (modifications.isEmpty()) {
                log.info("🔧 Aucune modification avec méthode simple, tentative avec filtrage manuel");
                modifications = tousStatuts.stream()
                        .filter(statut -> !statut.isInitialisation()) // Exclure l'initialisation
                        .filter(statut -> !statut.getDateEffet().isAfter(dateReference)) // Date <= date référence
                        .sorted((s1, s2) -> s1.getDateEffet().compareTo(s2.getDateEffet())) // Par date croissante
                        .collect(Collectors.toList());
                log.info("📊 Modifications trouvées (filtrage manuel): {}", modifications.size());
            }

            // 5. Désérialiser le contrat initial
            ContratEmploye contratInitial = getContratFromSnapshot(contratInitialStatut);
            contratInitial = chargerRelationsManquantes(contratInitial);
            log.info("✅ Contrat initial chargé - ID: {}", contratInitial.getId());

            // 6. Appliquer les modifications
            ContratEmploye contratFinal = appliquerModifications(contratInitial, modifications);
            contratFinal = chargerRelationsManquantes(contratFinal);
            log.info("✅ Contrat final préparé");

            // 7. Préparer le résultat
            ContratConsolideDTO result = new ContratConsolideDTO();
            result.setContratInitial(contratInitial);
            result.setContratFinal(contratFinal);
            result.setModifications(convertToModificationsDTO(modifications));

            log.info("🎉 Consolidation terminée - {} modification(s) appliquée(s)", modifications.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Erreur lors de la consolidation", e);
            throw e;
        }
    }

    private ContratEmploye getContratFromSnapshot(StatutContrat statut) throws Exception {
        log.info("🔧 Désérialisation snapshot pour statut ID: {}", statut.getId());

        try {
            String json = statut.getSnapshotJson();
            ObjectMapper mapper = createConfiguredObjectMapper(); // Utiliser le mapper configuré
            JsonNode rootNode = mapper.readTree(json);

            log.info("🏗️ Structure détectée:");
            rootNode.fieldNames().forEachRemaining(field -> log.info("   - {}", field));

            // Vérifier si c'est la nouvelle structure avec "contratEmploye"
            if (rootNode.has("contratEmploye")) {
                log.info("📋 Détection structure avec contratEmploye");
                return deserializeNouvelleStructure(rootNode, statut);
            } else {
                log.info("📋 Détection structure directe");
                return deserializeAncienneStructure(rootNode);
            }

        } catch (Exception e) {
            log.error("❌ Erreur désérialisation", e);
            throw e;
        }
    }

    /**
     * Désérialise la nouvelle structure avec "contratEmploye" comme objet principal
     */
    private ContratEmploye deserializeNouvelleStructure(JsonNode rootNode, StatutContrat statut) throws Exception {
        JsonNode contratNode = rootNode.get("contratEmploye");

        if (contratNode == null) {
            throw new IllegalArgumentException("Noeud contratEmploye non trouvé");
        }

        ContratEmploye contrat = new ContratEmploye();

        // ESSAYER DE TROUVER LE VRAI ID DU CONTRAT
        Long vraiId = trouverVraiIdContrat(statut);
        contrat.setId(vraiId != null ? vraiId : 1L);
        log.info("🔧 ID du contrat: {}", contrat.getId());

        // Peupler avec les champs de contratEmploye
        if (contratNode.has("typeContrat") && !contratNode.get("typeContrat").isNull()) {
            contrat.setType_contrat(contratNode.get("typeContrat").asText());
            log.info("🔧 Type contrat: {}", contrat.getType_contrat());
        }

        if (contratNode.has("salaire_base") && !contratNode.get("salaire_base").isNull()) {
            contrat.setSalaire_base(contratNode.get("salaire_base").asDouble());
            log.info("🔧 Salaire base: {}", contrat.getSalaire_base());
        } else if (contratNode.has("salaireBrut")) {
            contrat.setSalaire_base(contratNode.get("salaireBrut").asDouble());
        }

        if (contratNode.has("salaire_brut") && !contratNode.get("salaire_brut").isNull()) {
            contrat.setSalaire_brut(contratNode.get("salaire_brut").asDouble());
            log.info("🔧 Salaire brut: {}", contrat.getSalaire_brut());
        }

        if (contratNode.has("dateDebut") && !contratNode.get("dateDebut").isNull()) {
            contrat.setDate_debut(LocalDate.parse(contratNode.get("dateDebut").asText()));
            log.info("🔧 Date début: {}", contrat.getDate_debut());
        } else if (contratNode.has("date_debut")) {
            contrat.setDate_debut(LocalDate.parse(contratNode.get("date_debut").asText()));
        }

        if (contratNode.has("dateFin") && !contratNode.get("dateFin").isNull()) {
            contrat.setDate_fin(LocalDate.parse(contratNode.get("dateFin").asText()));
            log.info("🔧 Date fin: {}", contrat.getDate_fin());
        } else if (contratNode.has("date_fin")) {
            contrat.setDate_fin(LocalDate.parse(contratNode.get("date_fin").asText()));
        }

        if (contratNode.has("diplome_requis") && !contratNode.get("diplome_requis").isNull()) {
            contrat.setDiplome_requis(contratNode.get("diplome_requis").asText());
        }

        if (contratNode.has("missions") && !contratNode.get("missions").isNull()) {
            contrat.setMissions(contratNode.get("missions").asText());
        }

        if (contratNode.has("lieu_execution") && !contratNode.get("lieu_execution").isNull()) {
            contrat.setLieu_execution(contratNode.get("lieu_execution").asText());
        }

        return contrat;
    }

    /**
     * Désérialise l'ancienne structure (désérialisation directe)
     */
    private ContratEmploye deserializeAncienneStructure(JsonNode rootNode) throws Exception {
        ObjectMapper mapper = createConfiguredObjectMapper(); // Utiliser le mapper configuré
        return mapper.treeToValue(rootNode, ContratEmploye.class);
    }

    /**
     * Trouve le vrai ID du contrat via le statut
     */
    private Long trouverVraiIdContrat(StatutContrat statut) {
        // Méthode 1: Via la relation contratEmploye du statut
        if (statut.getContratEmploye() != null && statut.getContratEmploye().getId() != null) {
            log.info("🔍 ID trouvé via relation statut: {}", statut.getContratEmploye().getId());
            return statut.getContratEmploye().getId();
        }

        // Méthode 2: Chercher par employé ID dans le repository
        try {
            if (statut.getContratEmploye() != null && statut.getContratEmploye().getEmploye() != null) {
                Optional<ContratEmploye> contratExistant = contratEmployeRepository.findByEmployeIdWithAllRelations(
                        statut.getContratEmploye().getEmploye().getId()
                );
                if (contratExistant.isPresent()) {
                    log.info("🔍 ID trouvé via repository: {}", contratExistant.get().getId());
                    return contratExistant.get().getId();
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur recherche ID via repository", e);
        }

        log.warn("⚠️ Aucun ID trouvé, utilisation de l'ID temporaire");
        return null;
    }

    private ContratEmploye appliquerModifications(ContratEmploye contratInitial, List<StatutContrat> modifications) throws Exception {
        // Au lieu de cloner par sérialisation JSON, créer une copie manuelle
        ContratEmploye contratCourant = copierContratManuellement(contratInitial);

        for (StatutContrat modification : modifications) {
            contratCourant = appliquerModification(contratCourant, modification);
            log.info("🔧 Modification appliquée - Type: {}, Date: {}",
                    modification.getTypeModification(), modification.getDateEffet());
        }

        return contratCourant;
    }

    private ContratEmploye appliquerModification(ContratEmploye contrat, StatutContrat modification) throws Exception {
        ContratEmploye nouveauEtat = getContratFromSnapshot(modification);

        // CHARGER LES RELATIONS POUR LE NOUVEL ÉTAT AUSSI
        nouveauEtat = chargerRelationsManquantes(nouveauEtat);

        if (modification.getTypeModification() != null) {
            switch (modification.getTypeModification()) {
                case DEPARTEMENT_POSTE:
                    if (nouveauEtat.getDepartement() != null) {
                        contrat.setDepartement(nouveauEtat.getDepartement());
                    }
                    if (nouveauEtat.getPoste() != null) {
                        contrat.setPoste(nouveauEtat.getPoste());
                    }
                    break;

                case TYPE_CONTRAT:
                    if (nouveauEtat.getType_contrat() != null) {
                        contrat.setType_contrat(nouveauEtat.getType_contrat());
                    }
                    break;

                case DATE_FIN_CONTRAT:
                    if (nouveauEtat.getDate_fin() != null) {
                        contrat.setDate_fin(nouveauEtat.getDate_fin());
                    }
                    break;

                case CATEGORIE_EMPLOYE:
                    if (nouveauEtat.getCategorieEmploye() != null) {
                        contrat.setCategorieEmploye(nouveauEtat.getCategorieEmploye());
                    }
                    break;
            }
        }

        return contrat;
    }

    /**
     * Charge les relations manquantes après désérialisation
     */
    private ContratEmploye chargerRelationsManquantes(ContratEmploye contrat) {
        if (contrat.getId() != null && !contrat.getId().equals(1L)) {
            try {
                Optional<ContratEmploye> contratComplet = contratEmployeRepository.findByIdWithAllRelations(contrat.getId());
                if (contratComplet.isPresent()) {
                    ContratEmploye result = contratComplet.get();

                    // Initialiser les relations lazy
                    initializeLazyRelations(result);
                    return result;
                }
            } catch (Exception e) {
                log.error("❌ Erreur chargement relations", e);
            }
        }
        return contrat;
    }

    /**
     * Initialise les relations Hibernate lazy loading
     */
    private void initializeLazyRelations(ContratEmploye contrat) {
        try {
            if (contrat.getEmploye() != null) {
                Hibernate.initialize(contrat.getEmploye());
                // Initialiser les relations de l'employé si nécessaire
                if (contrat.getModeDePaiement() != null) {
                    Hibernate.initialize(contrat.getModeDePaiement());
                }
            }
            if (contrat.getCompany() != null) {
                Hibernate.initialize(contrat.getCompany());
            }
            if (contrat.getDepartement() != null) {
                Hibernate.initialize(contrat.getDepartement());
            }
            if (contrat.getPoste() != null) {
                Hibernate.initialize(contrat.getPoste());
            }
            if (contrat.getCategorieEmploye() != null) {
                Hibernate.initialize(contrat.getCategorieEmploye());
            }
            if (contrat.getNatureContrat() != null) {
                Hibernate.initialize(contrat.getNatureContrat());
            }
            if (contrat.getModeDePaiement() != null) {
                Hibernate.initialize(contrat.getModeDePaiement());
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur initialisation relations lazy", e);
        }
    }

    /**
     * Copie manuelle d'un contrat pour éviter la sérialisation JSON des proxies Hibernate
     */
    private ContratEmploye copierContratManuellement(ContratEmploye original) {
        ContratEmploye copie = new ContratEmploye();

        // Copier les champs simples
        copie.setId(original.getId());
        copie.setType_contrat(original.getType_contrat());
        copie.setSalaire_base(original.getSalaire_base());
        copie.setSalaire_brut(original.getSalaire_brut());
        copie.setDate_debut(original.getDate_debut());
        copie.setDate_fin(original.getDate_fin());
        copie.setDiplome_requis(original.getDiplome_requis());
        copie.setMissions(original.getMissions());
        copie.setLieu_execution(original.getLieu_execution());

        // Copier les relations (références seulement, pas de deep copy)
        copie.setEmploye(original.getEmploye());
        copie.setCompany(original.getCompany());
        copie.setDepartement(original.getDepartement());
        copie.setPoste(original.getPoste());
        copie.setCategorieEmploye(original.getCategorieEmploye());
        copie.setNatureContrat(original.getNatureContrat());
        copie.setModeDePaiement(original.getModeDePaiement());

        return copie;
    }

    /**
     * Clone un contrat en utilisant JSON serialization/deserialization (méthode alternative)
     * À utiliser seulement si la copie manuelle ne suffit pas
     */
    private ContratEmploye cloneContrat(ContratEmploye original) throws Exception {
        try {
            ObjectMapper mapper = createConfiguredObjectMapper();

            // Créer un DTO simplifié pour éviter les problèmes de proxy Hibernate
            ContratEmployeDTO dto = createSimplifiedDTO(original);

            String json = mapper.writeValueAsString(dto);
            ContratEmployeDTO dtoCopy = mapper.readValue(json, ContratEmployeDTO.class);

            return convertDTOToEntity(dtoCopy, original);

        } catch (Exception e) {
            log.warn("⚠️ Échec du clonage JSON, utilisation de la copie manuelle", e);
            return copierContratManuellement(original);
        }
    }

    /**
     * DTO simplifié pour éviter les problèmes de sérialisation
     */
    private static class ContratEmployeDTO {
        public Long id;
        public String type_contrat;
        public Double salaire_base;
        public Double salaire_brut;
        public LocalDate date_debut;
        public LocalDate date_fin;
        public String diplome_requis;
        public String missions;
        public String lieu_execution;
        // Ajouter d'autres champs simples si nécessaire
    }

    private ContratEmployeDTO createSimplifiedDTO(ContratEmploye contrat) {
        ContratEmployeDTO dto = new ContratEmployeDTO();
        dto.id = contrat.getId();
        dto.type_contrat = contrat.getType_contrat();
        dto.salaire_base = contrat.getSalaire_base();
        dto.salaire_brut = contrat.getSalaire_brut();
        dto.date_debut = contrat.getDate_debut();
        dto.date_fin = contrat.getDate_fin();
        dto.diplome_requis = contrat.getDiplome_requis();
        dto.missions = contrat.getMissions();
        dto.lieu_execution = contrat.getLieu_execution();
        return dto;
    }

    private ContratEmploye convertDTOToEntity(ContratEmployeDTO dto, ContratEmploye original) {
        ContratEmploye contrat = new ContratEmploye();
        contrat.setId(dto.id);
        contrat.setType_contrat(dto.type_contrat);
        contrat.setSalaire_base(dto.salaire_base);
        contrat.setSalaire_brut(dto.salaire_brut);
        contrat.setDate_debut(dto.date_debut);
        contrat.setDate_fin(dto.date_fin);
        contrat.setDiplome_requis(dto.diplome_requis);
        contrat.setMissions(dto.missions);
        contrat.setLieu_execution(dto.lieu_execution);

        // Conserver les relations de l'original
        contrat.setEmploye(original.getEmploye());
        contrat.setCompany(original.getCompany());
        contrat.setDepartement(original.getDepartement());
        contrat.setPoste(original.getPoste());
        contrat.setCategorieEmploye(original.getCategorieEmploye());
        contrat.setNatureContrat(original.getNatureContrat());
        contrat.setModeDePaiement(original.getModeDePaiement());

        return contrat;
    }

    private List<ModificationContrat> convertToModificationsDTO(List<StatutContrat> statuts) {
        return statuts.stream().map(statut -> {
            ModificationContrat mod = new ModificationContrat();
            mod.setStatut(statut);
            mod.setTypeModification(statut.getTypeModification() != null ? statut.getTypeModification().name() : null);
            mod.setDateEffet(statut.getDateEffet());
            mod.setMotif(statut.getMotif());
            mod.setPreuveFilename(statut.getPreuve());
            return mod;
        }).collect(Collectors.toList());
    }
}