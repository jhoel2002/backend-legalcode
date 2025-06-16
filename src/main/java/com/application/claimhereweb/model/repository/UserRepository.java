package com.application.claimhereweb.model.repository;

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

}