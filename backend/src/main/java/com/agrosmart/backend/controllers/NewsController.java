package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.AgriNews;
import com.agrosmart.backend.repositories.AgriNewsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    private final AgriNewsRepository newsRepository;

    public NewsController(AgriNewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public List<AgriNews> getAllNews(@RequestParam(required = false) String category) {
        if (category != null && !category.equalsIgnoreCase("All")) {
            return newsRepository.findByCategoryIgnoreCase(category);
        }
        return newsRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgriNews> getNewsById(@PathVariable String id) {
        return newsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
