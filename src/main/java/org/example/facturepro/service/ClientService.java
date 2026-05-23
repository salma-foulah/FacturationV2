package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Client;
import org.example.facturepro.entity.User;
import org.example.facturepro.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public List<Client> findPending() {
        return clientRepository.findByValidatedByAdmin(false);
    }

    public List<Client> findValidated() {
        return clientRepository.findByValidatedByAdmin(true);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + id));
    }

    public Client create(Client client, User currentUser) {
        client.setCreatedAt(LocalDateTime.now());
        // ADMIN users' clients are auto-validated, USER clients must await validation
        if ("ADMIN".equals(currentUser.getRole())) {
            client.setValidatedByAdmin(true);
            client.setValidatedAt(LocalDateTime.now());
        } else {
            client.setValidatedByAdmin(false);
        }
        return clientRepository.save(client);
    }

    public Client update(Long id, Client updated) {
        Client existing = findById(id);
        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());
        existing.setTelephone(updated.getTelephone());
        existing.setAdresse(updated.getAdresse());
        return clientRepository.save(existing);
    }

    public Client validate(Long id) {
        Client client = findById(id);
        client.setValidatedByAdmin(true);
        client.setValidatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        findById(id);
        clientRepository.deleteById(id);
    }

    public long countAll() {
        return clientRepository.count();
    }

    public long countPending() {
        return clientRepository.findByValidatedByAdmin(false).size();
    }

    public long countValidated() {
        return clientRepository.findByValidatedByAdmin(true).size();
    }
}
