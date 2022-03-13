package keeper.project.homepage.entity.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 45, nullable = false)
  private String title;

  @Column(length = 256)
  private String information;

  @Column(nullable = false)
  private Integer memberNumber;

  @Column
  private LocalDateTime registerTime;

  @Column
  private Integer year;

  @Column
  private Integer season;

  @Column(length = 256)
  private String gitLink;

  @Column(length = 256)
  private String noteLink;

  @Column(length = 256)
  private String etcLink;

  @OneToOne
  @JoinColumn(name = "thumbnail_id")
  @Setter
  private ThumbnailEntity thumbnail;

  @OneToMany(mappedBy = "study", orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<StudyHasMemberEntity> studyHasMemberEntities = new ArrayList<>();

  public String getThumbnailPath() {
    String thumbnailApiPath = "/v1/util/thumbnail/";
    if (getThumbnail() == null) {
      return thumbnailApiPath + 0;
    }
    return thumbnailApiPath + getThumbnail().getId();
  }

  public List<MemberDto> getStudyMembers() {
    List<MemberDto> members = new ArrayList<>();
    if (!getStudyHasMemberEntities().isEmpty()) {
      for (StudyHasMemberEntity studyHasMemberEntity : getStudyHasMemberEntities()) {
        MemberDto temp = new MemberDto();
        temp.initWithEntity(studyHasMemberEntity.getMember());
        members.add(temp);
      }
    }
    return members;
  }
}
