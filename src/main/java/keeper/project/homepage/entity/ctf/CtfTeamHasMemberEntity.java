package keeper.project.homepage.entity.ctf;

import java.io.Serializable;
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
@IdClass(CtfTeamHasMemberEntityPK.class) // 정의한 idclass 주입
@Table(name = "ctf_team_has_member")
public class CtfTeamHasMemberEntity implements Serializable {

  @Id
  @ManyToOne(targetEntity = CtfTeamEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "ctf_team_id")
  private CtfTeamEntity team;

  @Id
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private MemberEntity member;
}
