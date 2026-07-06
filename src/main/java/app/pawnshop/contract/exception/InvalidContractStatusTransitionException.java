package app.pawnshop.contract.exception;

import app.pawnshop.contract.model.ContractStatus;

public class InvalidContractStatusTransitionException extends RuntimeException {

    public InvalidContractStatusTransitionException(ContractStatus current, ContractStatus next) {
        super("Invalid contract status transition from " + current + " to " + next);
    }
}
