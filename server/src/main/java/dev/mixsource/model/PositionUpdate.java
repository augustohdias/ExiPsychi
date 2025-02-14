package dev.mixsource.model;

import lombok.Data;

@Data
public class PositionUpdate {
    private Long characterId;
    private Double x;
    private Double y;
    private Integer sequenceNumber;
} 