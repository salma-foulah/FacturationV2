package org.example.facturepro.repository;

import org.example.facturepro.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByValidatedByAdmin(Boolean validated);
}
