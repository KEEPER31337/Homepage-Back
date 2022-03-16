package keeper.project.homepage.entity.study;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StudyHasMemberEntityPK.class) // 정의한 idclass 주입
@Table(name = "study_has_member")
public class StudyHasMemberEntity implements Serializable {

  @Id
  @ManyToOne(targetEntity = StudyEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id")
  private StudyEntity study;

  @Id
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private MemberEntity member;

  @Column
  private LocalDateTime registerTime;
}
