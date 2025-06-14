package com.application.claimhereweb.model.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.Lawyer;

public interface LawyerRepository extends JpaRepository<Lawyer, Long> {

    // Buscar abogado por ID de usuario
    Optional<Lawyer> findByUserId(Long userId);

    // Obtener el nombre del usuario asociado al abogado por ID
    @Query("SELECT l.user.name FROM Lawyer l WHERE l.id = :id")
    String findLawyerUserNameById(@Param("id") Long id);

    // Búsqueda por texto en múltiples campos del usuario y tipo de caso
    @Query("""
                SELECT l FROM Lawyer l
                JOIN l.user u
                JOIN u.buffet b
                WHERE (
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(CAST(l.case_type AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                AND b.code = :codeBuffet
            """)
    Page<Lawyer> searchLawyerByBuffetCode(
            @Param("search") String search,
            @Param("codeBuffet") String codeBuffet,
            Pageable pageable);

    // Búsqueda por rango de fechas de creación del usuario
    @Query("""
                SELECT l FROM Lawyer l
                JOIN l.user u
                JOIN u.buffet b
                WHERE u.creation BETWEEN :startDate AND :endDate
                  AND b.code = :codeBuffet
            """)
    Page<Lawyer> findLawyerByUserCreationDateBetweenAndBuffetCode(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("codeBuffet") String codeBuffet,
            Pageable pageable);

    // Búsqueda combinada: texto + rango de fechas
    @Query("""
                SELECT l FROM Lawyer l
                JOIN l.user u
                JOIN u.buffet b
                WHERE (
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(CAST(l.case_type AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                AND u.creation BETWEEN :startDate AND :endDate
                AND b.code = :codeBuffet
            """)
    Page<Lawyer> searchLawyerByUserCreationDateBetweenAndBuffetCode(
            @Param("search") String search,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("codeBuffet") String codeBuffet,
            Pageable pageable);

    @Query("""
                SELECT l FROM Lawyer l
                JOIN l.user u
                JOIN u.buffet b
                WHERE b.code = :codeBuffet
            """)
    Page<Lawyer> findByBuffetCode(@Param("codeBuffet") String codeBuffet, Pageable pageable);
}