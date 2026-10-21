package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.CropPrice;
import com.agrosmart.backend.repositories.CropPriceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "*")
public class CropController {

    private final CropPriceRepository cropPriceRepository;

    public CropController(CropPriceRepository cropPriceRepository) {
        this.cropPriceRepository = cropPriceRepository;
    }

    @GetMapping("/prices")
    public List<CropPrice> getPrices(@RequestParam(required = false) String search,
                                     @RequestParam(required = false) String state) {
        if (search != null && !search.trim().isEmpty()) {
            return cropPriceRepository.findByCropNameContainingIgnoreCase(search);
        }
        if (state != null && !state.equalsIgnoreCase("All States")) {
            return cropPriceRepository.findByStateIgnoreCase(state);
        }
        return cropPriceRepository.findAll();
    }

    @PostMapping("/prices")
    public CropPrice savePrice(@RequestBody CropPrice price) {
        return cropPriceRepository.save(price);
    }
}
