package keeper.project.homepage.entity.member;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MemberPostingEntityPK.class) // 정의한 idclass 주입
@Table(name = "member_has_posting_dislike")
public class MemberHasPostingDislikeEntity implements Serializable {

  @Id
  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;

  @Id
  @ManyToOne(targetEntity = PostingEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "posting_id")
  private PostingEntity postingId;
}
