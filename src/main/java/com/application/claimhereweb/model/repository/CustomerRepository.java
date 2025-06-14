package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.claimhereweb.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

        Optional<Customer> findByUserId(Long userId);

        @Query("SELECT c.user.name FROM Customer c WHERE c.id = :id")
        String findCustomerUserNameById(@Param("id") Long id);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            JOIN u.buffet b
                            WHERE (
                                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(c.document_number) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(STR(c.document_type)) LIKE LOWER(CONCAT('%', :search, '%'))
                            )
                            AND b.code = :codeBuffet
                        """)
        Page<Customer> searchCustomersByBuffetCode(
                        @Param("search") String search,
                        @Param("codeBuffet") String codeBuffet,
                        Pageable pageable);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            JOIN u.buffet b
                            WHERE b.code = :codeBuffet
                        """)
        Page<Customer> findByBuffetCode(@Param("codeBuffet") String codeBuffet, Pageable pageable);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            JOIN u.buffet b
                            WHERE b.code = :codeBuffet
                              AND (
                                   LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                              )
                        """)
        List<Customer> searchCustomerByUserCodeOrNameOrLastName(
                        @Param("search") String search,
                        @Param("codeBuffet") String codeBuffet);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            JOIN u.buffet b
                            WHERE u.creation BETWEEN :startDate AND :endDate
                              AND b.code = :codeBuffet
                        """)
        Page<Customer> findCustomersByUserCreationDateBetweenAndBuffetCode(
                        @Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate,
                        @Param("codeBuffet") String codeBuffet,
                        Pageable pageable);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            JOIN u.buffet b
                            WHERE (
                                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(c.document_number) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(STR(c.document_type)) LIKE LOWER(CONCAT('%', :search, '%'))
                            )
                            AND u.creation BETWEEN :startDate AND :endDate
                            AND b.code = :codeBuffet
                        """)
        Page<Customer> searchCustomersByUserCreationDateBetweenAndBuffetCode(
                        @Param("search") String search,
                        @Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate,
                        @Param("codeBuffet") String codeBuffet,
                        Pageable pageable);

}