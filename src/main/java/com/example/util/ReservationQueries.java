package com.example.util;

import com.example.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationQueries {

    private final EntityManager em;

    public ReservationQueries(EntityManager em) {
        this.em = em;
    }

    // Etape 9 : JPQL
    public List<Reservation> parJpql() {
        String jpql = "SELECT r FROM Reservation r WHERE r.salle.nom = :nomSalle AND r.dateDebut > :date";
        return em.createQuery(jpql, Reservation.class)
                .setParameter("nomSalle", "Salle A")
                .setParameter("date", LocalDateTime.now())
                .getResultList();
    }

    // Etape 9 : Criteria API
    public List<Reservation> parCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> cq = cb.createQuery(Reservation.class);
        Root<Reservation> root = cq.from(Reservation.class);
        Predicate predicate = cb.and(
                cb.equal(root.get("salle").get("nom"), "Salle A"),
                cb.greaterThan(root.get("dateDebut"), LocalDateTime.now())
        );
        cq.select(root).where(predicate);
        return em.createQuery(cq).getResultList();
    }

    // Etape 10 : transaction RESOURCE_LOCAL + verrouillage optimiste
    public void modifierAvecTransaction(Long id, LocalDateTime nouvelleFin) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reservation r = em.find(Reservation.class, id);
            r.setDateFin(nouvelleFin);
            tx.commit();
        } catch (OptimisticLockException ole) {
            tx.rollback();
            System.out.println("Conflit détecté, veuillez réessayer.");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
