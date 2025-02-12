package dev.mixsource.model;

public class Monster {
    private Integer powerPoints;

    private Integer healthPoints;

    private Integer soulPoints;

    private Integer dodgePoints;

    private Long id;

    private Integer map;

    private Integer x;

    private Integer y;

    public Monster(final Integer x, final Integer y, final Long id, final String name) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public Integer map() {
        return x;
    }

    public Monster map(final Integer x) {
        this.x = x;
        return this;
    }

    public Integer x() {
        return x;
    }

    public Monster x(final Integer x) {
        this.x = x;
        return this;
    }

    public Integer y() {
        return y;
    }

    public Monster y(final Integer y) {
        this.y = y;
        return this;
    }
    public Integer powerPoints() {
        return powerPoints;
    }

    public Monster powerPoints(Integer powerPoints) {
        this.powerPoints = powerPoints;
        return this;
    }

    public Integer healthPoints() {
        return healthPoints;
    }

    public Monster healthPoints(Integer healthPoints) {
        this.healthPoints = healthPoints;
        return this;
    }

    public Integer soulPoints() {
        return soulPoints;
    }

    public Monster soulPoints(Integer soulPoints) {
        this.soulPoints = soulPoints;
        return this;
    }

    public Integer dodgePoints() {
        return dodgePoints;
    }

    public Monster dodgePoints(Integer dodgePoints) {
        this.dodgePoints = dodgePoints;
        return this;
    }
}
