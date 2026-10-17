package com.example.test;

import com.example.model.*;
import com.example.util.DataInitializer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

import org.junit.jupiter.api.*;

public class ReservationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    public static void init() {
        emf = Persistence.createEntityManagerFactory("ReservationPU");

        // La salle d'id 1 doit exister : on charge le jeu de données (étape 3)
        EntityManager initEm = emf.createEntityManager();
        new DataInitializer(initEm).initData();
        initEm.close();
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        if (em != null) em.close();
    }

    @AfterAll
    public static void close() {
        if (emf != null) emf.close();
    }

    @Test
    public void testCreateReservation() {
        em.getTransaction().begin();

        Utilisateur user = new Utilisateur();
        user.setNom("Test User");
        user.setEmail("test.user@example.com");
        em.persist(user);

        Salle salle = em.find(Salle.class, 1L); // Supposons qu'elle existe

        Reservation reservation = new Reservation();
        reservation.setUtilisateur(user);
        reservation.setSalle(salle);
        reservation.setDateDebut(LocalDateTime.now().plusDays(2));
        reservation.setDateFin(LocalDateTime.now().plusDays(2).plusHours(1));

        em.persist(reservation);
        em.getTransaction().commit();

        Assertions.assertNotNull(reservation.getId());
    }
}
