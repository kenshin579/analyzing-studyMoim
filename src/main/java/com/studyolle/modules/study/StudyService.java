package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.event.StudyCreatedEvent;
import com.studyolle.modules.study.event.StudyUpdateEvent;
import com.studyolle.modules.study.settings.Form.DescriptionForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.studyolle.modules.study.StudyForm.VALID_PATH_PATTERN;


@Transactional
@RequiredArgsConstructor
@Service
public class StudyService {
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Study saveStudy(Study study, Account account){
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public void updateDescription(Study study, DescriptionForm descriptionForm) {
        modelMapper.map(descriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
    }

    @Transactional(readOnly = true)
    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        checkIfManager(study, account);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudy(String path) {
        Study study = this.studyRepository.findStudyWithManagersByPath(path);
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

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findAccountWithZonesByPath(path);
        checkIfExistingStudy(study, path);
        checkIfManager(study, account);
        return study;
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = studyRepository.findStudyWithManagersByPath(path);
        checkIfExistingStudy(study, path);
        checkIfManager(study, account);
        return study;
    }

    public Study getStudyToEnroll(String path) {
        Study study = studyRepository.findStudyOnlyByPath(path);
        checkIfExistingStudy(study,path);
        return study;
    }

    public void addTag(Study study, Tag isTag) {
        study.getTags().add(isTag);
    }
    public void removeTag(Study study, Tag isTag) {
        study.getTags().remove(isTag);
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
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    public void nonPublished(Study study) {
        study.stopPublish();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디를 종료했습니다."));
    }

    public void recruiting(Study study) {
        study.recruiting();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 인원 모집이 시작되었습니다."));
    }

    public void stopRecruiting(Study study) {
        study.stopRecruiting();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 인원 모집이 종료되었습니다."));
    }

    public void removeStudy(Study study) {
        if(study.isRemoveable()){
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

    public void addMember(Study study, Account account) {
        study.addMember(account);
    }

    public void leaveMember(Study study, Account account) {
        study.removeMember(account);
    }
}
