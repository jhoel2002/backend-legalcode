package com.application.claimhereweb.model.repository;

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
}
