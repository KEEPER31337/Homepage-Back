package keeper.project.homepage.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import keeper.project.homepage.entity.identifier.MemberHasCommentLikeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_has_comment_like")
public class MemberHasCommentLikeEntity {

  @EmbeddedId
  private MemberHasCommentLikeId memberHasCommentLikeId;
}
