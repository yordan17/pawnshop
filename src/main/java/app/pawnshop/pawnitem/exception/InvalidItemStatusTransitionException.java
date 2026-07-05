package app.pawnshop.pawnitem.exception;

import app.pawnshop.pawnitem.model.ItemStatus;

public class InvalidItemStatusTransitionException extends RuntimeException {

    public InvalidItemStatusTransitionException(ItemStatus current, ItemStatus next) {
        super("Invalid status transition from " + current + " to " + next);
    }
}
