package com.dev.config;

import com.dev.models.Event;
import com.dev.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VoucherExpirationTask {
    @Autowired
    private EventRepository eventRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc nửa đêm
    public void checkVoucherExpirations() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> expiredVouchers = eventRepository.findAllByEndTimeBeforeAndActiveTrue(now);

        for (Event event : expiredVouchers) {
            event.setActive(false);
            eventRepository.save(event);
        }
    }
}
