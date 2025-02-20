package dev.mixsource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Monster {
    private Integer powerPoints;

    private Integer healthPoints;

    private Integer soulPoints;

    private Integer dodgePoints;

    private Long id;

    private Integer x;

    private Integer y;
}
