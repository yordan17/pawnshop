package app.pawnshop.payment.exception;

import app.pawnshop.contract.model.ContractStatus;

import java.util.UUID;

public class ContractNotActiveException extends RuntimeException {

    public ContractNotActiveException(UUID contractId, ContractStatus status) {
        super("Contract " + contractId + " is not active. Current status: " + status);
    }
}
