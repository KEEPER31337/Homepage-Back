package keeper.project.homepage.entity.ctf;

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
import javax.persistence.UniqueConstraint;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_team",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "contest_id"}))
public class CtfTeamEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Setter
  @Column(nullable = false, length = 45)
  String name;

  @Setter
  @Column(length = 200)
  String description;

  @Column(nullable = false)
  LocalDateTime registerTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator")
  MemberEntity creator;

  @Column(nullable = false)
  @Setter
  Long score;

  @OneToOne
  @JoinColumn(name = "contest_id")
  CtfContestEntity ctfContestEntity;

  @Builder.Default
  @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
  List<CtfTeamHasMemberEntity> ctfTeamHasMemberEntityList = new ArrayList<>();
}
