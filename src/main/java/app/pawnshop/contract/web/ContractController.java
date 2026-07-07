package app.pawnshop.contract.web;

import app.pawnshop.contract.dto.ContractRequest;
import app.pawnshop.contract.exception.ContractNotFoundException;
import app.pawnshop.contract.exception.InvalidContractStatusTransitionException;
import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.service.ContractService;
import app.pawnshop.payment.service.PaymentService;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.service.PawnItemService;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final PawnItemService pawnItemService;
    private final PaymentService paymentService;

    @GetMapping
    public String list(Model model) {
        List<Contract> contracts = contractService.getAllContracts();
        Map<UUID, BigDecimal> interestMap = new HashMap<>();
        for (Contract contract : contracts) {
            interestMap.put(contract.getId(), contractService.calculateAccruedInterest(contract));
        }
        model.addAttribute("contracts", contracts);
        model.addAttribute("interestMap", interestMap);
        return "contracts/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("contractRequest", new ContractRequest());
        model.addAttribute("pawnItems", pawnItemService.getPawnItemsByStatus(ItemStatus.AVAILABLE));
        return "contracts/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("contractRequest") ContractRequest request,
                         BindingResult bindingResult,
                         Authentication authentication,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pawnItems", pawnItemService.getPawnItemsByStatus(ItemStatus.AVAILABLE));
            return "contracts/form";
        }
        contractService.createContract(request, authentication.getName());
        log.info("Contract created for pawn item: {}", request.getPawnItemId());
        return "redirect:/contracts";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        Contract contract = contractService.getContractById(id);
        java.math.BigDecimal accruedInterest = contractService.calculateAccruedInterest(contract);
        model.addAttribute("contract", contract);
        model.addAttribute("accruedInterest", accruedInterest);
        model.addAttribute("totalDue", contract.getLoanAmount().add(accruedInterest));
        model.addAttribute("payments", paymentService.getPaymentsByContractId(id));
        model.addAttribute("statuses", ContractStatus.values());
        return "contracts/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Contract contract = contractService.getContractById(id);
        ContractRequest request = new ContractRequest();
        request.setStartDate(contract.getStartDate());
        request.setDueDate(contract.getDueDate());
        request.setPawnItemId(contract.getPawnItem().getId());
        model.addAttribute("contractRequest", request);
        model.addAttribute("contractId", id);
        model.addAttribute("contract", contract);
        return "contracts/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("contractRequest") ContractRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            Contract contract = contractService.getContractById(id);
            model.addAttribute("contractId", id);
            model.addAttribute("contract", contract);
            return "contracts/form";
        }
        contractService.updateContract(id, request);
        log.info("Contract updated: {}", id);
        return "redirect:/contracts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            contractService.deleteContract(id);
            log.info("Contract deleted: {}", id);
        } catch (ContractNotFoundException e) {
            log.warn("Failed to delete contract {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/contracts/" + id;
        }
        return "redirect:/contracts";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable UUID id,
                               @RequestParam ContractStatus newStatus,
                               RedirectAttributes redirectAttributes) {
        try {
            contractService.changeContractStatus(id, newStatus);
            log.info("Contract {} status changed to {}", id, newStatus);
        } catch (InvalidContractStatusTransitionException e) {
            log.warn("Invalid status transition for contract {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/contracts/" + id;
    }
}
