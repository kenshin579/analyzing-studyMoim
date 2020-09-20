package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;

    public Event createEvent(Study study, Event event, Account account) {
        event.setCreatedDateTime(LocalDateTime.now());
        event.setCreateBy(account);
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public Map<String, List<Event>> getEvents(Study study) {
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        Map<String, List<Event>> map = new HashMap<>();
        map.put("newEvents", events.stream().filter(a -> a.getEndDateTime().isAfter(LocalDateTime.now())).collect(Collectors.toList()));
        map.put("oldEvents", events.stream().filter(a -> a.getEndDateTime().isBefore(LocalDateTime.now())).collect(Collectors.toList()));
        return map;
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow();
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
}
