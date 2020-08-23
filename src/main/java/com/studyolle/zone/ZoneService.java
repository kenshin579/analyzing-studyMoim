package com.studyolle.zone;

import com.studyolle.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ZoneService {
    private final ZoneRepository zoneRepository;
    @PostConstruct
    public List<Zone> initZoneData() throws IOException {
        if(zoneRepository.count() == 0 ) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    }).collect(Collectors.toList());
                zoneRepository.saveAll(zoneList);
        }
        return zoneRepository.findAll();
    }

    public List<String> getAllZones() {
        return zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
    }

    public Zone getZoneToUpdateProfile(String cityName, String provinceName){
        return zoneRepository.findByCityAndProvince(cityName, provinceName);
    }

    public Zone getZoneToUpdateStudy(String cityName, String provinceName) {
        Zone zone = zoneRepository.findByCityAndProvince(cityName, provinceName);
        checkExistingZone(zone);
        return zone;
    }

    private void checkExistingZone(Zone zone){
        if(zone == null){
            throw new IllegalArgumentException("일치하는 지역 정보가 없습니다.");
        }
    }
}
