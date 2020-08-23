package com.studyolle.tag;

import com.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    public List<String> getAllTag(){
        return tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
    }

    public Tag isTagThere(String tag){
        return this.tagRepository.findByTitle(tag);
    }

    public Tag saveTag(String tag){
        return this.tagRepository.save(Tag.builder().title(tag).build());
    }
}
