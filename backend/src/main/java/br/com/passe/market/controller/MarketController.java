package br.com.passe.market.controller;

import br.com.passe.market.dto.BuyRequest;
import br.com.passe.market.dto.BuyResponse;
import br.com.passe.market.dto.SellResponse;
import br.com.passe.market.service.PurchaseService;
import br.com.passe.market.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class MarketController {

    private final PurchaseService purchaseService;
    private final SaleService saleService;

    @PostMapping("/{id}/buy")
    public BuyResponse buy(@PathVariable String id, @Valid @RequestBody BuyRequest request) {
        return purchaseService.buy(id, request);
    }

    @PostMapping("/{id}/sell")
    public SellResponse sell(@PathVariable String id) {
        return saleService.sell(id);
    }
}
