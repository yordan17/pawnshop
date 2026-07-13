package app.pawnshop.payment.web;

import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.service.ContractService;
import app.pawnshop.payment.dto.PaymentRequest;
import app.pawnshop.payment.exception.ContractNotActiveException;
import app.pawnshop.payment.exception.PaymentNotFoundException;
import app.pawnshop.payment.model.Payment;
import app.pawnshop.payment.model.PaymentType;
import app.pawnshop.payment.service.PaymentService;
import app.pawnshop.user.model.User;
import app.pawnshop.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ContractService contractService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payments/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        model.addAttribute("payment", paymentService.getPaymentById(id));
        return "payments/details";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) UUID preselectedContractId, Model model) {
        PaymentRequest request = new PaymentRequest();
        request.setPaymentDate(LocalDate.now());
        if (preselectedContractId != null) {
            request.setContractId(preselectedContractId);
        }

        List<Contract> activeContracts = contractService.getContractsByStatus(ContractStatus.ACTIVE);

        model.addAttribute("paymentRequest", request);
        model.addAttribute("activeContracts", activeContracts);
        model.addAttribute("contractDataMap", buildContractDataMap(activeContracts));
        model.addAttribute("paymentTypes", PaymentType.values());
        return "payments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("paymentRequest") PaymentRequest request,
                         BindingResult bindingResult,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        List<Contract> activeContracts = contractService.getContractsByStatus(ContractStatus.ACTIVE);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeContracts", activeContracts);
            model.addAttribute("contractDataMap", buildContractDataMap(activeContracts));
            model.addAttribute("paymentTypes", PaymentType.values());
            return "payments/form";
        }

        User currentUser = userService.getUserByUsername(authentication.getName());
        request.setReceivedById(currentUser.getId());

        try {
            Payment payment = paymentService.createPayment(request);
            log.info("Payment {} created by {}", payment.getId(), authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Плащането е регистрирано успешно.");
        } catch (ContractNotActiveException e) {
            log.warn("Payment creation failed - contract not active: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("activeContracts", activeContracts);
            model.addAttribute("contractDataMap", buildContractDataMap(activeContracts));
            model.addAttribute("paymentTypes", PaymentType.values());
            return "payments/form";
        }

        return "redirect:/payments";
    }

    private Map<String, Object> buildContractDataMap(List<Contract> contracts) {
        Map<String, Object> contractDataMap = new HashMap<>();
        for (Contract contract : contracts) {
            Map<String, Object> data = new HashMap<>();
            data.put("loanAmount", contract.getLoanAmount());
            data.put("interestRate", contract.getInterestRate());
            data.put("startDate", contract.getStartDate().toString());
            data.put("dueDate", contract.getDueDate().toString());
            contractDataMap.put(contract.getId().toString(), data);
        }
        return contractDataMap;
    }
}
