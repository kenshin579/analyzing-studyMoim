package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withMembers", attributeNodes = {
        @NamedAttributeNode("members")
})
@AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(of = "id")
@Builder @Getter @Setter
@Entity
public class Study {
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers  = new HashSet<Account>();;

    @ManyToMany
    private Set<Account> members  = new HashSet<Account>();;

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<Tag>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<Zone>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;
    private int memberCount;

    public void addManager(Account account) {
        this.managers.add(account);
    }
    public boolean isJoinable(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting() && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount account){
        return this.members.contains(account.getAccount());
    }

    public boolean isManager(UserAccount userAccount){
        return this.managers.contains(userAccount.getAccount());
    }

    public boolean isManagerAccount(Account account) {
        return this.managers.contains(account);
    }

    public boolean isRemoveable(){return !this.published && !this.recruiting;}

    public boolean isPossibleUpdatePublished(){
        if(this.publishedDateTime != null){
            return this.publishedDateTime.plusHours(1).isBefore(LocalDateTime.now());
        }
        return true;
    }

    public boolean isPossibleUpdateRecruiting(){
        if(this.recruitingUpdateDateTime != null) {
            return this.recruitingUpdateDateTime.plusHours(1).isBefore(LocalDateTime.now());
        }
        return true;
    }

    public void publish(){
        if(!this.published ){
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
        }
    }

    public void nonPublish(){
        if(this.published && !this.recruiting && this.isPossibleUpdatePublished()){
            this.published = false;
            this.publishedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 종료할 수 없습니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
        }
    }

    public void recruiting(){
        if(this.published && !this.recruiting && this.isPossibleUpdateRecruiting()){
            this.recruiting = true;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤에 다시 시도하세요.");
        }
    }

    public void nonRecruiting(){
        if(this.published && this.recruiting && this.isPossibleUpdateRecruiting()){
            this.recruiting = false;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("인원 모집을 중지할 수 없습니다. 스터디를 공개하거나 한 시간 뒤에 다시 시도하세요.");
        }
    }

    public void addMember(Account account){
        this.getMembers().add(account);
        this.memberCount++;
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }
}
