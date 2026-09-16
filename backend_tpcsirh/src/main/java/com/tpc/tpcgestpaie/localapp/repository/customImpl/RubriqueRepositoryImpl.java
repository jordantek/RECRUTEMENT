package com.tpc.tpcgestpaie.localapp.repository.customImpl;

import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.repository.custom.RubriqueRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RubriqueRepositoryImpl implements RubriqueRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer getDernierNumeroOrdre() {
        return (Integer) entityManager
                .createQuery("SELECT MAX(r.numeroOrdre) FROM Rubrique r")
                .getSingleResult();
    }

    @Override
    public List<Rubrique> getRubriqueStartByLibelle(String libelle) {
        return entityManager
                .createQuery("FROM Rubrique r WHERE r.libelle LIKE :libelle", Rubrique.class)
                .setParameter("libelle", "%" + libelle + "%")
                .getResultList();
    }

    @Override
    public List<Rubrique> getAllRubriquePourEtatSalaire() {
        return entityManager
                .createQuery("FROM Rubrique r ORDER BY r.niveau_affichage_id, r.numeroOrdre", Rubrique.class)
                .getResultList();
    }

    @Override
    public List<Rubrique> getAllRubriquePourEnregistrementMontantMensuelRubrique() {
        return entityManager
                .createQuery("""
                    FROM Rubrique r 
                    WHERE r.libelle NOT IN (
                        'FORFAIT HEURES SUPPLEMENTAIRE', 
                        'AUGMENTATION SALARIALE',
                        'INDEMNITES DE CONGES',
                        'PRIME D''ANCIENNETE',
                        'MENSUALITES',
                        'AVANCE',
                        'SALAIRE DE BASE',
                        'HONORAIRE DE BASE',
                        'TRANSFERT',
                        'HONORAIRE BRUT IMPOSABLE'
                    )
                    ORDER BY r.id
                """, Rubrique.class)
                .getResultList();
    }
}
