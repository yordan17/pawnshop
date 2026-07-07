package app.pawnshop.customer.service;

import app.pawnshop.customer.dto.CustomerRequest;
import app.pawnshop.customer.exception.CustomerNotFoundException;
import app.pawnshop.customer.exception.DuplicateCustomerException;
import app.pawnshop.customer.model.Customer;
import app.pawnshop.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Email already registered: " + request.getEmail());
        }
        if (customerRepository.existsByPersonalId(request.getPersonalId())) {
            throw new DuplicateCustomerException("Personal ID already registered: " + request.getPersonalId());
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .personalId(request.getPersonalId())
                .city(request.getCity())
                .street(request.getStreet())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer created with id: {}", saved.getId());
        return saved;
    }

    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateCustomerException("Email already registered: " + request.getEmail());
        }
        if (!customer.getPersonalId().equals(request.getPersonalId())
                && customerRepository.existsByPersonalId(request.getPersonalId())) {
            throw new DuplicateCustomerException("Personal ID already registered: " + request.getPersonalId());
        }

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setPersonalId(request.getPersonalId());
        customer.setCity(request.getCity());
        customer.setStreet(request.getStreet());

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated with id: {}", updated.getId());
        return updated;
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted with id: {}", id);
    }
}
