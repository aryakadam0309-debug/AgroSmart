package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<NotificationItem> getNotifications(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return notificationRepository.findByFarmerPhoneOrFarmerPhoneIsNullOrderByTimestampDesc(phone.trim());
        }
        return notificationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<NotificationItem> createNotification(@RequestBody NotificationItem item) {
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId("NOTIF-" + System.currentTimeMillis());
        }
        return ResponseEntity.ok(notificationRepository.save(item));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        return notificationRepository.findById(id).map(n -> {
            n.setRead(true);
            notificationRepository.save(n);
            return ResponseEntity.ok(n);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam(required = false) String phone) {
        List<NotificationItem> list;
        if (phone != null && !phone.trim().isEmpty()) {
            list = notificationRepository.findByFarmerPhone(phone.trim());
        } else {
            list = notificationRepository.findAll();
        }
        for (NotificationItem n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
