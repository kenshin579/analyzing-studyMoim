package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.study.settings.Form.DescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.studyolle.study.StudyForm.VALID_PATH_PATTERN;


@Transactional
@RequiredArgsConstructor
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
    }

    @Transactional(readOnly = true)
    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        checkIfManager(study, account);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistingStudy(study, path);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findTagsByPath(path);
        checkIfExistingStudy(study, path);
        checkIfManager(study, account);
        return study;
    }
    public void addTag(Study study, Tag isTag) {
        study.getTags().add(isTag);
    }
    public void removeTag(Study study, Tag isTag) {
        study.getTags().remove(isTag);
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findAccountWithZonesByPath(path);
        checkIfExistingStudy(study, path);
        checkIfManager(study, account);
        return study;
    }
    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }
    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    private void checkIfExistingStudy(Study study, String path){
        if(study == null){
            throw new IllegalArgumentException(path + " 해당 스터디는 존재하지 않습니다.");
        }
    }
    private void checkIfManager(Study study, Account account){
        if(!study.getManagers().contains(account)){
            throw new AccessDeniedException("해당 계정은 스터디를 수정할 수 없습니다.");
        }
    }

    public void updateStudyName(Study study, String title) {
        study.setTitle(title);
    }

    public void updateStudyPath(Study study, String path) {
        study.setPath(path);
    }

    public void publishStudy(Study study) {
        study.publish();
    }

    public void nonPublished(Study study) {
        study.nonPublish();
    }

    public void recruiting(Study study) {
        study.recruiting();
    }

    public void nonRecruiting(Study study) {
        study.nonRecruiting();
    }

    public void removeStudy(Study study) {
        if(study.isRemovable()){
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public boolean isValidPath(String newPath) {
        if(!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        return !studyRepository.existsByPath(newPath);
    }
}
