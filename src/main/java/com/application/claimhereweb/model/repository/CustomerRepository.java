package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.claimhereweb.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

        @Query("SELECT c.user.name FROM Customer c WHERE c.id = :id")
        String findCustomerUserNameById(@Param("id") Long id);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(c.document) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(STR(c.type_document_customer)) LIKE LOWER(CONCAT('%', :search, '%'))
                        """)
        Page<Customer> searchCustomers(@Param("search") String search, Pageable pageable);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            WHERE u.creation BETWEEN :startDate AND :endDate
                        """)
        Page<Customer> findCustomersByUserCreationDateBetween(
                        @Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate,
                        Pageable pageable);

        @Query("""
                            SELECT c FROM Customer c
                            JOIN c.user u
                            WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(c.document) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(STR(c.type_document_customer)) LIKE LOWER(CONCAT('%', :search, '%')))
                              AND u.creation BETWEEN :startDate AND :endDate
                        """)
        Page<Customer> searchCustomersByUserCreationDateBetween(
                        @Param("search") String search,
                        @Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate,
                        Pageable pageable);
}
