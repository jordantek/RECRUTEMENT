package com.tpc.tpcgestpaie.localapp.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

@Component
public class RequestHelper {

    private final HttpServletRequest request;
    private final ApplicationContext applicationContext;

    @Autowired
    public RequestHelper(HttpServletRequest request, ApplicationContext applicationContext) {
        this.request = request;
        this.applicationContext = applicationContext;
    }

    public String getClientIp() {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }

    public boolean areRequiredFieldsPresent(Map<String, Object> requestBody, String... requiredFields) {
        if (requestBody == null) {
            return false;
        }

        for (String field : requiredFields) {
            if (!requestBody.containsKey(field)) {
                return false;
            }
        }
        return true;
    }

    //Fonction pour vérifier si un champ est vide
    public List<String> getMissingFields(Map<String, Object> requestBody, String... requiredFields) {
        List<String> missingFields = new ArrayList<>();

        if (requestBody == null) {
            return Arrays.asList(requiredFields); // tous les champs sont manquants
        }

        for (String field : requiredFields) {
            if (!requestBody.containsKey(field)) {
                missingFields.add(field);
            }
        }
        return missingFields;
    }

    public boolean isFieldEmpty(Map<String, Object> requestBody, String fieldName) {
        if (requestBody == null || !requestBody.containsKey(fieldName)) {
            return true;
        }

        Object value = requestBody.get(fieldName);

        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value) == 0;
        }

        // Pour les types numériques, on considère null comme vide, mais pas 0 ou autre
        if (value instanceof Number) {
            return false;
        }

        // Pour tous les autres types, s'ils ne sont pas null, on ne les considère pas vides
        return false;
    }

    // Vérifier si l'email est valide et non déjà utilisé
    public boolean isValidEmail(String email) {
        return !StringUtils.hasText(email);
    }


    @Transactional()
    public boolean isValueExists(Class<?> entityClass, String attributeName, Object value) {
        try {
            // Récupérer le nom simple de l'entité pour construire le nom du repository
            String entityName = entityClass.getSimpleName();
            String repositoryBeanName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "Repository";

            // Tenter de récupérer le repository depuis le contexte de l'application
            if (applicationContext.containsBean(repositoryBeanName)) {
                JpaRepository<?, ?> repository = (JpaRepository<?, ?>) applicationContext.getBean(repositoryBeanName);

                // Construction du nom de la méthode à appeler dynamiquement
                String methodName = "existsBy" + capitalizeFirstLetter(attributeName);

                // Rechercher la méthode appropriée en fonction du type de la valeur
                Method method = null;
                try {
                    method = repository.getClass().getMethod(methodName, value.getClass());
                } catch (NoSuchMethodException e) {
                    // Gérer le cas où aucune méthode correspondante n'est trouvée directement avec le type de la valeur
                    // On pourrait essayer de chercher une méthode avec le type de l'attribut de l'entité si nécessaire
                    return false;
                }

                // Appel de la méthode via réflexion
                if (method != null) {
                    return (boolean) method.invoke(repository, value);
                }
            }
            return false; // Repository non trouvé
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean isValueExistsExcept(Class<?> entityClass, String attributeName, Object value, Long excludeId) {
        try {
            String entityName = entityClass.getSimpleName();
            String repositoryBeanName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "Repository";

            if (applicationContext.containsBean(repositoryBeanName)) {
                JpaRepository<?, ?> repository = (JpaRepository<?, ?>) applicationContext.getBean(repositoryBeanName);

                // Construire le nom de la méthode "findByXxx"
                String methodName = "findBy" + capitalizeFirstLetter(attributeName);

                // On cherche une méthode findByXXX qui renvoie l’entité
                Method method = null;
                try {
                    method = repository.getClass().getMethod(methodName, value.getClass());
                } catch (NoSuchMethodException e) {
                    return false; // méthode introuvable
                }

                if (method != null) {
                    Object entity = method.invoke(repository, value);
                    if (entity != null) {
                        // Récupérer l'ID de l'entité trouvée
                        Method getIdMethod = entity.getClass().getMethod("getId");
                        Long foundId = (Long) getIdMethod.invoke(entity);

                        // Si l’ID trouvé est différent de excludeId → conflit
                        return !foundId.equals(excludeId);
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // Méthode utilitaire pour capitaliser la première lettre d'une chaîne
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}