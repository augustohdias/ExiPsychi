package dev.mixsource.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "characters", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int x;

    private int y;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
