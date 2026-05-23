package org.example.facturepro.repository;

import org.example.facturepro.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.statut = :statut")
    Long countByStatut(String statut);

    @Query("SELECT COALESCE(SUM(i.totalHt), 0) FROM Invoice i WHERE i.statut = :statut")
    Double sumTotalHtByStatut(String statut);

    @Query("SELECT COALESCE(SUM(i.totalTtc), 0) FROM Invoice i WHERE i.statut = :statut")
    Double sumTotalTtcByStatut(String statut);

    @Query("SELECT COALESCE(SUM(i.totalHt), 0) FROM Invoice i")
    Double sumAllTotalHt();

    @Query("SELECT COALESCE(SUM(i.totalTtc), 0) FROM Invoice i")
    Double sumAllTotalTtc();
}
