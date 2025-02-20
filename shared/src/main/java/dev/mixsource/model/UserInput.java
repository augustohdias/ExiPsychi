package dev.mixsource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
    private List<Command> issuedCommands;
    private LocalDateTime clientTime;
    private long sequenceNumber;
}
