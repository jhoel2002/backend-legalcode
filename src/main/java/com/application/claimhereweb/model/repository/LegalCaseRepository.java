package com.application.claimhereweb.model.repository;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.LegalCase;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {

    // @Query("SELECT c.user.name FROM Customer c WHERE c.id = :id")
    @Query("SELECT lc.title FROM LegalCase lc WHERE lc.id = :id")
    String findTitleById(@Param("id") Long id);

    /*
     * select u.id from users u
     * inner join users_roles ur on u.id = ur.id_users
     * inner join roles r on r.id = ur.id_role
     * where r."name" = 'ROLE_LAWYER';
     */

    @Query("SELECT u.id FROM User u JOIN u.roles r WHERE r.name = 'ROLE_LAWYER' AND u.id = :id")
    Long findLawyerIdByUserId(@Param("id") Long id);

    @Query("""
                SELECT lc FROM LegalCase lc
                WHERE lc.start_date BETWEEN :startDate AND :endDate
            """)
    Page<LegalCase> findCaseByStartDateBetween(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);

    @Query("""
                SELECT lc FROM LegalCase lc
                WHERE LOWER(lc.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(lc.description) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(STR(lc.type_case)) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(STR(lc.status_case)) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<LegalCase> searchLegalCase(@Param("search") String search, Pageable pageable);

    @Query("""
                SELECT lc FROM LegalCase lc
                WHERE (LOWER(lc.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(lc.description) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(STR(lc.type_case)) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(STR(lc.status_case)) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND lc.start_date BETWEEN :startDate AND :endDate
            """)
    Page<LegalCase> listFilterFull(
            @Param("search") String search,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);

}
