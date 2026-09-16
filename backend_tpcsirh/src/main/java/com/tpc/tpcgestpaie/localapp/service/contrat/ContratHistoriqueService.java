package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class ContratHistoriqueService {

    private static final Logger log = LoggerFactory.getLogger(ContratHistoriqueService.class);

    private final StatutContratRepository statutContratRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final ObjectMapper objectMapper;

    public ContratHistoriqueService(StatutContratRepository statutContratRepository,
                                    ContratEmployeRepository contratEmployeRepository,
                                    ObjectMapper objectMapper) {
        this.statutContratRepository = statutContratRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Récupère le contrat tel qu'il était à une date spécifique
     */

    public ContratEmploye getContratByDate(Long employeId, LocalDate dateReference) throws Exception {
        log.info("🔍 DEBUT - Recherche contrat pour employé ID: {} à la date: {}", employeId, dateReference);

        try {
            // 1. Trouver le statut actif à la date référence
            Optional<StatutContrat> statut = statutContratRepository.findStatutByEmployeAndDate(employeId, dateReference);
            log.info("📊 Statut trouvé: {}", statut.isPresent());

            if (statut.isPresent()) {
                StatutContrat statutTrouve = statut.get();
                log.info("📅 Statut ID: {}, Date effet: {}", statutTrouve.getId(), statutTrouve.getDateEffet());
                log.info("💾 Snapshot disponible: {}", statutTrouve.getSnapshotJson() != null);

                if (statutTrouve.getSnapshotJson() != null) {
                    log.info("📄 Longueur snapshot: {}", statutTrouve.getSnapshotJson().length());
                    // 2. Si un snapshot existe à cette date, le désérialiser
                    ContratEmploye contratFromSnapshot = getContratFromSnapshot(statutTrouve);
                    log.info("✅ Contrat from snapshot - ID: {}, Type: {}",
                            contratFromSnapshot.getId(), contratFromSnapshot.getType_contrat());
                    return contratFromSnapshot;
                } else {
                    log.info("⚠️ Pas de snapshot, utilisation du contrat actuel du statut");
                    ContratEmploye contratActuel = statutTrouve.getContratEmploye();
                    log.info("✅ Contrat actuel du statut - ID: {}, Type: {}",
                            contratActuel.getId(), contratActuel.getType_contrat());
                    return contratActuel;
                }
            } else {
                log.info("🔍 Aucun statut trouvé, utilisation du contrat actuel de l'employé");
                // 3. Sinon, utiliser le contrat actuel de l'employé
                ContratEmploye contratActuel = getContratActuel(employeId);
                log.info("✅ Contrat actuel de l'employé - ID: {}, Type: {}",
                        contratActuel.getId(), contratActuel.getType_contrat());
                return contratActuel;
            }

        } catch (Exception e) {
            log.error("❌ Erreur dans getContratByDate", e);
            throw e;
        }
    }

    private ContratEmploye getContratFromSnapshot(StatutContrat statut) throws Exception {
        try {
            String snapshotJson = statut.getSnapshotJson();
            log.info("🔧 DEBUT désérialisation snapshot");

            // Afficher les premiers caractères du JSON pour debug
            log.info("📋 Début du JSON: {}", snapshotJson.substring(0, Math.min(200, snapshotJson.length())));

            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Désérialiser
            ContratEmploye contratHistorique = objectMapper.readValue(snapshotJson, ContratEmploye.class);

            log.info("🔧 APRÈS désérialisation - ID: {}, Type: {}",
                    contratHistorique.getId(), contratHistorique.getType_contrat());

            // Charger les relations
            ContratEmploye contratAvecRelations = chargerRelationsManquantes(contratHistorique);

            log.info("🔧 APRÈS chargement relations - ID: {}, Type: {}",
                    contratAvecRelations.getId(), contratAvecRelations.getType_contrat());

            return contratAvecRelations;

        } catch (Exception e) {
            log.error("❌ Erreur désérialisation snapshot", e);
            throw e;
        }
    }

    private ContratEmploye chargerRelationsManquantes(ContratEmploye contrat) {
        log.info("🔧 DEBUT chargement relations pour contrat ID: {}", contrat.getId());

        if (contrat.getId() != null) {
            try {
                Optional<ContratEmploye> contratComplet = contratEmployeRepository.findByIdWithAllRelations(contrat.getId());
                if (contratComplet.isPresent()) {
                    ContratEmploye result = contratComplet.get();
                    log.info("✅ Relations chargées - ID: {}, Employé: {}",
                            result.getId(),
                            result.getEmploye() != null ? result.getEmploye().getNom() : "null");
                    return result;
                } else {
                    log.warn("⚠️ Contrat ID: {} non trouvé en base", contrat.getId());
                    return contrat;
                }
            } catch (Exception e) {
                log.error("❌ Erreur chargement relations", e);
                return contrat;
            }
        } else {
            log.warn("⚠️ Contrat sans ID, impossible de charger les relations");
            return contrat;
        }
    }

    // Mixin pour ignorer le champ contratEmploye
    @JsonIgnoreProperties({"contratEmploye", "hibernateLazyInitializer", "handler"})
    private abstract class IgnoreContratEmployeMixin {}

    // Méthode de fallback avec nettoyage du JSON
    private ContratEmploye deserializeWithCleanup(String snapshotJson) throws Exception {
        try {
            // Nettoyer le JSON en supprimant le champ problématique
            String cleanedJson = snapshotJson.replaceAll("\"contratEmploye\"[^,}]*[,]?", "");

            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ContratEmploye contrat = objectMapper.readValue(cleanedJson, ContratEmploye.class);
            log.info("✅ Désérialisation réussie après nettoyage JSON");
            return chargerRelationsManquantes(contrat);

        } catch (Exception e) {
            log.error("❌ Échec même après nettoyage JSON", e);
            throw new Exception("Impossible de désérialiser le snapshot même après nettoyage: " + e.getMessage(), e);
        }
    }
    /**
     * Charge les relations manquantes après désérialisation
     */

    private ContratEmploye getContratActuel(Long employeId) {
        try {
            Optional<ContratEmploye> contrat = contratEmployeRepository.findByEmployeIdWithAllRelations(employeId);
            if (contrat.isPresent()) {
                log.info("✅ Contrat actuel trouvé ID: {}", contrat.get().getId());
                return contrat.get();
            } else {
                log.error("❌ Aucun contrat trouvé pour l'employé ID: {}", employeId);
                throw new IllegalArgumentException("Aucun contrat trouvé pour l'employé ID: " + employeId);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du contrat actuel pour l'employé ID: {}", employeId, e);
            throw new IllegalArgumentException("Erreur lors de la récupération du contrat actuel: " + e.getMessage(), e);
        }
    }
}