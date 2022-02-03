package keeper.project.homepage.entity.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder                    // builder를 사용할수 있게 합니다.
@Entity                     // jpa entity임을 알립니다.
@Getter                     // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor          // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor         // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member_rank")
public class MemberRankEntity implements Serializable {

  @Id // pk
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 45)
  private String name;

  @OneToMany(mappedBy = "memberRank", fetch = FetchType.EAGER)
  @JsonBackReference(value = "member-rank")
  @Builder.Default
  private List<MemberEntity> members = new ArrayList<>();

  public boolean addMember(MemberEntity memberEntity) {
    if (this.members.contains(memberEntity)) {
      return false;
    }
    return this.members.add(memberEntity);
  }

  public boolean removeMember(MemberEntity memberEntity) {
    return this.members.remove(memberEntity);
  }
}
