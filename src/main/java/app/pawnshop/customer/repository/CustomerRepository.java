package app.pawnshop.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByPersonalId(String personalId);

    boolean existsByEmail(String email);

    boolean existsByPersonalId(String personalId);
}
