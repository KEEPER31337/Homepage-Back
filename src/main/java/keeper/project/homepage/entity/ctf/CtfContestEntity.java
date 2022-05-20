package keeper.project.homepage.entity.ctf;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.admin.dto.ctf.CtfContestDto;
import keeper.project.homepage.common.mapper.CommonMemberMapper;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ctf_contest")
public class CtfContestEntity {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 45)
  String name;

  @Column(length = 200)
  String description;

  @Column(nullable = false)
  LocalDateTime registerTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator")
  MemberEntity creator;

  @Column(nullable = false)
  @Setter
  Boolean isJoinable;
}
