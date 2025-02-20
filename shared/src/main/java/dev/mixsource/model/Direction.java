package dev.mixsource.model;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    ;

    public float getRotationAngle() {
        return switch (this) {
            case UP -> 90f;
            case DOWN -> 270f;
            case LEFT ->  180f;
            case RIGHT -> 0f;  
        };
    }
}
