package org.example.facturepro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;
    private String companySiret;

    @Builder.Default
    private Double defaultTva = 20.0;

    @Builder.Default
    private String currency = "MAD";

    private String logoUrl;
}
