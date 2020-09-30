package com.studyolle.modules.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> getAllTag(){
        return tagRepository.findAll();
    }

    public Tag saveTag(String tag){
        return this.tagRepository.save(Tag.builder().title(tag).build());
    }

    public Tag findOrCreateTag(String tagTitle) {
        if(tagRepository.findByTitle(tagTitle) == null){
            this.saveTag(tagTitle);
        }
        return this.tagRepository.findByTitle(tagTitle);
    }
}
