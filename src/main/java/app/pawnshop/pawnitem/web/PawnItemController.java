package app.pawnshop.pawnitem.web;

import app.pawnshop.customer.service.CustomerService;
import app.pawnshop.pawnitem.dto.PawnItemRequest;
import app.pawnshop.pawnitem.exception.InvalidItemStatusTransitionException;
import app.pawnshop.pawnitem.exception.PawnItemNotFoundException;
import app.pawnshop.pawnitem.model.ItemCategory;
import app.pawnshop.pawnitem.model.ItemCondition;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.model.PawnItem;
import app.pawnshop.pawnitem.service.PawnItemService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/pawn-items")
@RequiredArgsConstructor
public class PawnItemController {

    private final PawnItemService pawnItemService;
    private final CustomerService customerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pawnItems", pawnItemService.getAllPawnItems());
        return "pawn-items/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("pawnItemRequest", new PawnItemRequest());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("categories", ItemCategory.values());
        model.addAttribute("conditions", ItemCondition.values());
        return "pawn-items/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("pawnItemRequest") PawnItemRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("categories", ItemCategory.values());
            model.addAttribute("conditions", ItemCondition.values());
            return "pawn-items/form";
        }
        pawnItemService.createPawnItem(request);
        log.info("Pawn item created: {}", request.getName());
        return "redirect:/pawn-items";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable UUID id, Model model) {
        model.addAttribute("pawnItem", pawnItemService.getPawnItemById(id));
        model.addAttribute("statuses", ItemStatus.values());
        return "pawn-items/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        PawnItem item = pawnItemService.getPawnItemById(id);
        PawnItemRequest request = new PawnItemRequest();
        request.setName(item.getName());
        request.setDescription(item.getDescription());
        request.setCategory(item.getCategory());
        request.setCondition(item.getCondition());
        request.setEstimatedValue(item.getEstimatedValue());
        request.setCustomerId(item.getCustomer().getId());
        model.addAttribute("pawnItemRequest", request);
        model.addAttribute("pawnItemId", id);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("categories", ItemCategory.values());
        model.addAttribute("conditions", ItemCondition.values());
        return "pawn-items/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("pawnItemRequest") PawnItemRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pawnItemId", id);
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("categories", ItemCategory.values());
            model.addAttribute("conditions", ItemCondition.values());
            return "pawn-items/form";
        }
        pawnItemService.updatePawnItem(id, request);
        log.info("Pawn item updated: {}", id);
        return "redirect:/pawn-items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            pawnItemService.deletePawnItem(id);
            log.info("Pawn item deactivated: {}", id);
        } catch (PawnItemNotFoundException e) {
            log.warn("Failed to delete pawn item {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/pawn-items";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable UUID id,
                               @RequestParam ItemStatus newStatus,
                               RedirectAttributes redirectAttributes) {
        try {
            pawnItemService.changeItemStatus(id, newStatus);
            log.info("Pawn item {} status changed to {}", id, newStatus);
        } catch (InvalidItemStatusTransitionException e) {
            log.warn("Invalid status transition for pawn item {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/pawn-items/" + id;
    }
}
