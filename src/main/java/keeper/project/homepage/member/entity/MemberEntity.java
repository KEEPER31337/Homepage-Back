package keeper.project.homepage.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import keeper.project.homepage.clerk.entity.SeminarAttendanceEntity;
import keeper.project.homepage.member.dto.MultiMemberResponseDto;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.study.entity.StudyHasMemberEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member")
@DynamicInsert
@DynamicUpdate
public class MemberEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "login_id", length = 80, nullable = false, unique = true)
  private String loginId;

  @Column(name = "email_address", length = 250, nullable = false, unique = true)
  private String emailAddress;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "password", length = 512, nullable = false)
  private String password;

  @Column(name = "real_name", length = 40, nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String realName;

  @Column(name = "nick_name", length = 40, nullable = false)
  private String nickName;

  @Column(name = "birthday")
  private Date birthday;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(name = "student_id", length = 45, nullable = false, unique = true)
  private String studentId;

  @CreationTimestamp
  @Column(name = "register_date")
  private Date registerDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_type_id")
  @NotFound(action = NotFoundAction.IGNORE)
  // DEFAULT 1
  private MemberTypeEntity memberType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_rank_id")
  @NotFound(action = NotFoundAction.IGNORE)
  // DEFAULT 1
  private MemberRankEntity memberRank;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "level", nullable = false)
  private Integer level;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id")
  private ThumbnailEntity thumbnail;

  @Column(name = "merit", nullable = false)
  private Integer merit;

  @Column(name = "demerit", nullable = false)
  private Integer demerit;

  @Column(name = "generation")
  private Float generation;

  @Column(name = "total_attendance", nullable = false)
  private Integer totalAttendance;

  @OneToMany(mappedBy = "followee", cascade = CascadeType.REMOVE)
  @Builder.Default
  private List<FriendEntity> follower = new ArrayList<>();

  @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE)
  @Builder.Default
  private List<FriendEntity> followee = new ArrayList<>();

  @OneToMany(mappedBy = "member", orphanRemoval = true)
  @Builder.Default
  private List<StudyHasMemberEntity> studyHasMemberEntities = new ArrayList<>();

  @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
  @Builder.Default
  private List<MemberHasMemberJobEntity> memberJobs = new ArrayList<>();

  @OneToMany(targetEntity = PostingEntity.class, mappedBy = "memberId")
  @Builder.Default
  private List<PostingEntity> posting = new ArrayList<>();

  @OneToMany(mappedBy = "memberEntity")
  @Builder.Default
  private List<SeminarAttendanceEntity> seminarAttendances = new ArrayList<>();

  @PrePersist
  private void prePersist() {
    this.point = (this.point == null ? 0 : this.point);
    this.level = (this.level == null ? 0 : this.level);
    this.merit = (this.merit == null ? 0 : this.merit);
    this.demerit = (this.demerit == null ? 0 : this.demerit);
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void changeRealName(String newRealName) {
    this.realName = newRealName;
  }

  public void changeNickName(String newNickName) {
    this.nickName = newNickName;
  }

  public void changeStudentId(String newStudentId) {
    this.studentId = newStudentId;
  }

  public void changeEmailAddress(String newEmailAddress) {
    this.emailAddress = newEmailAddress;
  }

  public void changeThumbnail(ThumbnailEntity newThumbnail) {
    this.thumbnail = newThumbnail;
  }

  public void changeMemberRank(MemberRankEntity memberRankEntity) {
    this.memberRank = memberRankEntity;
  }

  public void changeMemberType(MemberTypeEntity memberTypeEntity) {
    this.memberType = memberTypeEntity;
  }

  public void updatePoint(int point) {
    this.point = point;
  }

  public void changeMerit(int merit) {
    this.merit = merit;
  }

  public void changeDemerit(int demerit) {
    this.demerit = demerit;
  }

  public void changeGeneration(float generation) {
    this.generation = generation;
  }

  public List<String> getJobs() {
    List<String> jobs = new ArrayList<>();
    if (getMemberJobs() != null || getMemberJobs().isEmpty() == false) {
      getMemberJobs()
          .forEach(job -> jobs.add(job.getMemberJobEntity().getName()));
    }
    return jobs;
  }

  public String getThumbnailPath() {
    return getThumbnail() == null ?
        EnvironmentProperty.getThumbnailPath(ThumbType.MemberThumbnail.getDefaultThumbnailId())
        : EnvironmentProperty.getThumbnailPath(getThumbnail().getId());
  }

  public MultiMemberResponseDto toMultiMemberResponseDto() {
    return MultiMemberResponseDto.builder()
        .id(this.id)
        .nickName(this.nickName)
        .thumbnailPath(this.getThumbnailPath())
        .generation(this.generation)
        .jobs(this.getJobs())
        .type(this.memberType.getName())
        .msg("Success")
        .build();
  }

  public void addMemberJob(MemberJobEntity job) {
    MemberHasMemberJobEntity memberHasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberEntity(this)
        .memberJobEntity(job)
        .build();
    getMemberJobs().add(memberHasMemberJobEntity);
  }

  public void removeMemberJob(MemberJobEntity job) {
    getMemberJobs().removeIf(entity -> entity.getMemberJobEntity().equals(job));
    job.getMembers().removeIf(entity -> entity.getMemberEntity().equals(this));
  }
}