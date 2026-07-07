package app.pawnshop.pawnitem.service;

import app.pawnshop.customer.model.Customer;
import app.pawnshop.customer.repository.CustomerRepository;
import app.pawnshop.customer.exception.CustomerNotFoundException;
import app.pawnshop.pawnitem.dto.PawnItemRequest;
import app.pawnshop.pawnitem.exception.InvalidItemStatusTransitionException;
import app.pawnshop.pawnitem.exception.PawnItemNotFoundException;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.model.PawnItem;
import app.pawnshop.pawnitem.repository.PawnItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PawnItemService {

    private final PawnItemRepository pawnItemRepository;
    private final CustomerRepository customerRepository;

    public PawnItem createPawnItem(PawnItemRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        PawnItem item = PawnItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .condition(request.getCondition())
                .estimatedValue(request.getEstimatedValue())
                .interestRate(request.getInterestRate())
                .status(ItemStatus.AVAILABLE)
                .customer(customer)
                .active(true)
                .build();

        PawnItem saved = pawnItemRepository.save(item);
        log.info("Pawn item created with id: {}", saved.getId());
        return saved;
    }

    public PawnItem getPawnItemById(UUID id) {
        return pawnItemRepository.findById(id)
                .orElseThrow(() -> new PawnItemNotFoundException(id));
    }

    public List<PawnItem> getAllPawnItems() {
        return pawnItemRepository.findAll();
    }

    public List<PawnItem> getPawnItemsByStatus(ItemStatus status) {
        return pawnItemRepository.findByStatus(status);
    }

    public PawnItem updatePawnItem(UUID id, PawnItemRequest request) {
        PawnItem item = pawnItemRepository.findById(id)
                .orElseThrow(() -> new PawnItemNotFoundException(id));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setCondition(request.getCondition());
        item.setEstimatedValue(request.getEstimatedValue());
        item.setInterestRate(request.getInterestRate());
        item.setCustomer(customer);

        PawnItem updated = pawnItemRepository.save(item);
        log.info("Pawn item updated with id: {}", updated.getId());
        return updated;
    }

    public void deletePawnItem(UUID id) {
        if (!pawnItemRepository.existsById(id)) {
            throw new PawnItemNotFoundException(id);
        }
        pawnItemRepository.deleteById(id);
        log.info("Pawn item deleted with id: {}", id);
    }

    public PawnItem changeItemStatus(UUID id, ItemStatus newStatus) {
        PawnItem item = pawnItemRepository.findById(id)
                .orElseThrow(() -> new PawnItemNotFoundException(id));

        validateStatusTransition(item.getStatus(), newStatus);

        item.setStatus(newStatus);
        PawnItem updated = pawnItemRepository.save(item);
        log.info("Pawn item {} status changed from {} to {}", id, item.getStatus(), newStatus);
        return updated;
    }

    private void validateStatusTransition(ItemStatus current, ItemStatus next) {
        boolean valid = switch (current) {
            case AVAILABLE -> next == ItemStatus.PAWNED;
            case PAWNED -> next == ItemStatus.REDEEMED || next == ItemStatus.EXPIRED;
            case EXPIRED -> next == ItemStatus.SOLD;
            default -> false;
        };

        if (!valid) {
            throw new InvalidItemStatusTransitionException(current, next);
        }
    }
}
