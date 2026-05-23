package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Settings;
import org.example.facturepro.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<Settings> get() {
        return ResponseEntity.ok(settingsService.get());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Settings> update(@RequestBody Settings settings) {
        return ResponseEntity.ok(settingsService.update(settings));
    }
}
