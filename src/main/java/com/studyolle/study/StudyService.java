package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class StudyService {
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;
    public Study saveStudy(Study study, Account account){
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public void updateDescription(Study byPath, DescriptionForm descriptionForm) {
        modelMapper.map(descriptionForm, byPath);
        studyRepository.save(byPath);
    }
}
