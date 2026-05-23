package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Settings;
import org.example.facturepro.repository.SettingsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public Settings get() {
        return settingsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Paramètres introuvables"));
    }

    public Settings update(Settings updated) {
        Settings existing = get();
        existing.setCompanyName(updated.getCompanyName());
        existing.setCompanyAddress(updated.getCompanyAddress());
        existing.setCompanyPhone(updated.getCompanyPhone());
        existing.setCompanyEmail(updated.getCompanyEmail());
        existing.setCompanySiret(updated.getCompanySiret());
        existing.setDefaultTva(updated.getDefaultTva());
        existing.setCurrency(updated.getCurrency());
        existing.setLogoUrl(updated.getLogoUrl());
        return settingsRepository.save(existing);
    }
}
