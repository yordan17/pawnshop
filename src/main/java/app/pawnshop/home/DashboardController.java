package app.pawnshop.home;

import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.service.ContractService;
import app.pawnshop.customer.service.CustomerService;
import app.pawnshop.payment.service.PaymentService;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.service.PawnItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CustomerService customerService;
    private final ContractService contractService;
    private final PawnItemService pawnItemService;
    private final PaymentService paymentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCustomers", customerService.getAllCustomers().size());
        model.addAttribute("activeContracts", contractService.getContractsByStatus(ContractStatus.ACTIVE).size());
        model.addAttribute("pawnedItems", pawnItemService.getPawnItemsByStatus(ItemStatus.PAWNED).size());
        model.addAttribute("totalPayments", paymentService.getAllPayments().size());

        log.info("Dashboard loaded");
        return "dashboard";
    }
}
