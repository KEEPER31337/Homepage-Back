package keeper.project.homepage.entity.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasPostingLikeEntity;
import keeper.project.homepage.user.dto.member.MemberDto;
import keeper.project.homepage.util.EnvironmentProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "study")
public class StudyEntity {

  private static final Long RECTANGLE_DEFAULT_THUMBNAIL_ID = 1L;
  private static final Long SQUARE_DEFAULT_THUMBNAIL_ID = 2L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(length = 45, nullable = false)
  private String title;

  @Setter
  @Column(length = 256)
  private String information;

  @Setter
  @Column(nullable = false)
  private Integer memberNumber;

  @Column
  private LocalDateTime registerTime;

  @Setter
  @Column
  private Integer year;

  @Setter
  @Column
  private Integer season;

  @Setter
  @Column(length = 256)
  private String gitLink;

  @Setter
  @Column(length = 256)
  private String noteLink;

  @Setter
  @Column(length = 256)
  private String etcLink;

  @Setter
  @ManyToOne
  @JoinColumn(name = "thumbnail_id")
  private ThumbnailEntity thumbnail;

  @Setter
  @ManyToOne
  @JoinColumn(name = "head_member_id")
  private MemberEntity headMember;

  @OneToMany(mappedBy = "study", orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<StudyHasMemberEntity> studyHasMemberEntities = new ArrayList<>();

  public String getThumbnailPath() {

    if (getThumbnail() == null) {
      return EnvironmentProperty.getThumbnailPath(SQUARE_DEFAULT_THUMBNAIL_ID);
    }
    return EnvironmentProperty.getThumbnailPath(getThumbnail().getId());
  }

  public List<MemberDto> getStudyMembers() {
    List<MemberDto> members = new ArrayList<>();
    List<StudyHasMemberEntity> studyHasMemberEntities = getStudyHasMemberEntities();
    studyHasMemberEntities.sort(Comparator.comparing(StudyHasMemberEntity::getRegisterTime));
    if (!studyHasMemberEntities.isEmpty()) {
      for (StudyHasMemberEntity studyHasMemberEntity : studyHasMemberEntities) {
        MemberDto temp = new MemberDto();
        temp.initWithEntity(studyHasMemberEntity.getMember());
        members.add(temp);
      }
    }
    return members;
  }

  public MemberDto headMemberToDto() {
    MemberDto temp = new MemberDto();
    temp.initWithEntity(headMember);
    return temp;
  }
}
