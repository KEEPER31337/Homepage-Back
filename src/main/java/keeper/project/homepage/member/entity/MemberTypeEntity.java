package keeper.project.homepage.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import keeper.project.homepage.util.service.ThumbnailService.ThumbType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Builder                    // builder를 사용할수 있게 합니다.
@Entity                     // jpa entity임을 알립니다.
@Getter                     // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor          // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor         // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member_type")
public class MemberTypeEntity implements Serializable {

  @Id // pk
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 45)
  private String name;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "badge_thumbnail_id")
  private ThumbnailEntity badge;

  @OneToMany(mappedBy = "memberType", fetch = FetchType.EAGER)
  @JsonBackReference(value = "member-type")
  @Builder.Default
  private List<MemberEntity> members = new ArrayList<>();

  public String getBadgePath() {
    return getBadge() == null ?
        EnvironmentProperty.getThumbnailPath(ThumbType.Badge.getDefaultThumbnailId())
        : EnvironmentProperty.getThumbnailPath(getBadge().getId());
  }
  @Getter
  @RequiredArgsConstructor
  public enum memberType {
    NON_MEMBER(1L, "비회원"),
    REGULAR_MEMBER(2L, "정회원"),
    DORMANT_MEMBER(3L, "휴면회원"),
    GRADUATED_MEMBER(4L, "졸업"),
    WITHDRAWAL_MEMBER(5L, "탈퇴");

    private final Long id;
    private final String name;
  }
}
