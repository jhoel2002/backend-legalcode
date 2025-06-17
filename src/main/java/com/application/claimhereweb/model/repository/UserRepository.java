package com.application.claimhereweb.model.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByCode(String code);

    Optional<User> findByCode(String code);

    Optional<String> findBuffetCodeByEmail(String email);

    @Query("""
                SELECT u FROM User u
                JOIN u.roles r
                WHERE r.name IN :roleNames
                AND u.buffet.code = :codeBuffet
            """)
    Page<User> findByRolesInAndBuffetCode(@Param("roleNames") List<String> roleNames,
            @Param("codeBuffet") String codeBuffet,
            Pageable pageable);

    @Query("""
                SELECT u FROM User u
                JOIN u.roles r
                WHERE r.name IN :roleNames
                  AND u.creation BETWEEN :startDate AND :endDate
                  AND u.buffet.code = :codeBuffet
            """)
    Page<User> findByUserCreationDate(
            @Param("roleNames") List<String> roleNames,
            @Param("codeBuffet") String codeBuffet,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);

    @Query("""
                SELECT u FROM User u
                JOIN u.roles r
                WHERE r.name IN :roleNames
                  AND u.buffet.code = :codeBuffet
                  AND (
                      LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.address) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<User> findByUser(
            @Param("roleNames") List<String> roleNames,
            @Param("search") String search,
            @Param("codeBuffet") String codeBuffet,
            Pageable pageable);

    @Query("""
                SELECT u FROM User u
                JOIN u.roles r
                WHERE r.name IN :roleNames
                  AND u.buffet.code = :codeBuffet
                  AND (:startDate IS NULL OR :endDate IS NULL OR u.creation BETWEEN :startDate AND :endDate)
                  AND (
                      :search IS NULL OR :search = '' OR
                      LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                      LOWER(u.address) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<User> findUsersWithFilters(
            @Param("roleNames") List<String> roleNames,
            @Param("codeBuffet") String codeBuffet,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("search") String search,
            Pageable pageable);

}