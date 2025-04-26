package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.Facture;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    /*
     * @Query("SELECT l.title FROM Facture f JOIN f.legalCase l WHERE f.customer.id = :id"
     * )
     * String findTitleLegalCasebyCustomerId(@Param("id") Long id);
     */
}
