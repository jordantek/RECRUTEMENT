package com.tpc.tpcgestpaie.localapp.Initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.model.HrEventType;
import com.tpc.tpcgestpaie.localapp.repository.HrEventTypeRepository;
import com.tpc.tpcgestpaie.localapp.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HrEventTypeInitializer implements CommandLineRunner {

    private final HrEventTypeRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) {

        log.info("Initialisation des types d'événements RH...");

        List<EventSeed> seeds = buildSeeds();

        seeds.forEach(this::createIfNotExists);

        log.info("Initialisation terminée");
    }

    /**
     * Création uniquement si le slug n'existe pas
     */
    private void createIfNotExists(EventSeed seed) {

        String slug = SlugUtils.generateSlug(seed.label(), true, "_");

        boolean exists = repository.existsBySlug(slug);

        if (exists) {
            log.debug("Type déjà existant -> {}", slug);
            return;
        }

        // transformer la string en JSON
        // seed.color est de la forme "bg-orange-200 text-orange-600 border-orange-400"
        String[] parts = seed.color().split(" ");
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("bg", parts.length > 0 ? parts[0] : "");
        colorMap.put("text", parts.length > 1 ? parts[1] : "");
        colorMap.put("border", parts.length > 2 ? parts[2] : "");

        String colorJson = "{}";
        try {
            colorJson = objectMapper.writeValueAsString(colorMap);
        } catch (Exception e) {
            log.error("Erreur lors de la conversion du color en JSON pour {}", seed.label(), e);
        }

        HrEventType entity = HrEventType.builder()
                .label(seed.label())
                .slug(slug)
                .description(seed.description())
                .icon(seed.icon())
                .color(colorJson) // 🔹 JSON string ici
                .actionRequired(seed.actionRequired())
                .recurring(seed.recurring())
                .enabled(true)
                .build();

        repository.save(entity);

        log.info("Type RH créé -> {}", seed.label());
    }

    /**
     * Déclaration centralisée des seeds
     */
    private List<EventSeed> buildSeeds() {
        return List.of(
                // ===== CONTRAT =====
                seed("Début du contrat", "Entrée en fonction de l’employé",
                        "FileText", "bg-green-100 text-green-600 border-green-300", true, false),

                seed("Fin du contrat", "Fin officielle du contrat",
                        "CalendarX", "bg-red-100 text-red-600 border-red-300", true, false),

                seed("Renouvellement contrat", "Renouvellement contractuel",
                        "RefreshCcw", "bg-blue-100 text-blue-600 border-blue-300", true, false),

                seed("Anniversaire contrat", "Anniversaire de signature",
                        "CalendarCheck", "bg-indigo-100 text-indigo-600 border-indigo-300", false, true),

                seed("Fin période d’essai", "Validation ou rupture de la période d’essai",
                        "AlertTriangle", "bg-orange-100 text-orange-600 border-orange-300", true, false),

                // ===== EMPLOYÉ =====
                seed("Anniversaire employé", "Date anniversaire de naissance",
                        "Cake", "bg-pink-100 text-pink-600 border-pink-300", false, true),

                seed("Onboarding employé", "Arrivée du collaborateur",
                        "UserPlus", "bg-green-100 text-green-600 border-green-300", true, false),

                seed("Offboarding employé", "Départ du collaborateur",
                        "UserMinus", "bg-red-100 text-red-600 border-red-300", true, false),

                // ===== ABSENCES =====
                seed("Début absence", "Début absence déclarée",
                        "PlaneTakeoff", "bg-yellow-100 text-yellow-700 border-yellow-300", false, false),

                seed("Fin absence", "Retour absence",
                        "PlaneLanding", "bg-green-100 text-green-600 border-green-300", false, false),

                seed("Début congé", "Début congé planifié",
                        "Sun", "bg-yellow-100 text-yellow-700 border-yellow-300", false, false),

                seed("Fin congé", "Retour congé",
                        "Sunrise", "bg-green-100 text-green-600 border-green-300", false, false),

                // ===== PAIE =====
                seed("Révision salariale", "Réévaluation du salaire",
                        "Wallet", "bg-emerald-100 text-emerald-600 border-emerald-300", true, false),

                seed("Versement prime", "Paiement bonus / prime",
                        "Gift", "bg-teal-100 text-teal-600 border-teal-300", false, false),

                // ===== FORMATION =====
                seed("Début formation", "Lancement session formation",
                        "GraduationCap", "bg-blue-100 text-blue-600 border-blue-300", false, false),

                seed("Fin formation", "Fin session formation",
                        "BookCheck", "bg-purple-100 text-purple-600 border-purple-300", false, false),

                seed("Évaluation performance", "Entretien performance",
                        "BarChart3", "bg-violet-100 text-violet-600 border-violet-300", true, false),

                // ===== ADMIN =====
                seed("Expiration document", "Expiration document administratif",
                        "FileWarning", "bg-red-100 text-red-600 border-red-300", true, false),

                seed("Visite médicale", "Visite médicale obligatoire",
                        "Stethoscope", "bg-rose-100 text-rose-600 border-rose-300", true, false)
        );
    }

    /**
     * Factory simplifiée
     */
    private EventSeed seed(
            String label,
            String description,
            String icon,
            String color,
            boolean actionRequired,
            boolean recurring
    ) {
        return new EventSeed(label, description, icon, color, actionRequired, recurring);
    }

    /**
     * DTO interne (propre et immutable)
     */
    private record EventSeed(
            String label,
            String description,
            String icon,
            String color,
            boolean actionRequired,
            boolean recurring
    ) {}
}
