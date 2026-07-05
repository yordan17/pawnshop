package app.pawnshop.pawnitem.repository;

import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.model.PawnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PawnItemRepository extends JpaRepository<PawnItem, UUID> {

    List<PawnItem> findByCustomerId(UUID customerId);

    List<PawnItem> findByStatus(ItemStatus status);
}
