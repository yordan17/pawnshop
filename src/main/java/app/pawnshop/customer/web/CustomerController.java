package app.pawnshop.customer.web;

import app.pawnshop.customer.dto.CustomerRequest;
import app.pawnshop.customer.exception.CustomerNotFoundException;
import app.pawnshop.customer.exception.DuplicateCustomerException;
import app.pawnshop.customer.model.Customer;
import app.pawnshop.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("customerRequest", new CustomerRequest());
        return "customers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("customerRequest") CustomerRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "customers/form";
        }
        try {
            customerService.createCustomer(request);
            log.info("Customer created: {}", request.getEmail());
            return "redirect:/customers";
        } catch (DuplicateCustomerException e) {
            log.warn("Failed to create customer: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "customers/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "customers/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        CustomerRequest request = new CustomerRequest();
        request.setFirstName(customer.getFirstName());
        request.setLastName(customer.getLastName());
        request.setPhoneNumber(customer.getPhoneNumber());
        request.setEmail(customer.getEmail());
        request.setPersonalId(customer.getPersonalId());
        request.setAddress(customer.getAddress());
        model.addAttribute("customerRequest", request);
        model.addAttribute("customerId", id);
        return "customers/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("customerRequest") CustomerRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customerId", id);
            return "customers/form";
        }
        try {
            customerService.updateCustomer(id, request);
            log.info("Customer updated: {}", id);
            return "redirect:/customers";
        } catch (DuplicateCustomerException e) {
            log.warn("Failed to update customer {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customerId", id);
            return "customers/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            log.info("Customer deactivated: {}", id);
        } catch (CustomerNotFoundException e) {
            log.warn("Failed to delete customer {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }
}
