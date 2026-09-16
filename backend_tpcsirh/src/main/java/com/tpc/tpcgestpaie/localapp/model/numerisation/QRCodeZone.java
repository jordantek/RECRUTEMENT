package com.tpc.tpcgestpaie.localapp.model.numerisation;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class QRCodeZone {
    private Integer page = 1;
    private Float positionX; // Position X en mm
    private Float positionY; // Position Y en mm
    private Float width = 80f;
    private Float height = 80f;

    // Constructeur avec paramètres
    public QRCodeZone(Integer page, Float positionX, Float positionY, Float width, Float height) {
        this.page = page;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
    }

}