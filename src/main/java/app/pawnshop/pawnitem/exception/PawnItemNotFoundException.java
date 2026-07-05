package app.pawnshop.pawnitem.exception;

import java.util.UUID;

public class PawnItemNotFoundException extends RuntimeException {

    public PawnItemNotFoundException(UUID id) {
        super("Pawn item not found with id: " + id);
    }
}
