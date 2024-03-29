package keeper.project.homepage.member.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_has_comment_dislike")
public class MemberHasCommentDislikeEntity {

  @EmbeddedId
  private MemberHasCommentEntityPK memberHasCommentEntityPK;
}
