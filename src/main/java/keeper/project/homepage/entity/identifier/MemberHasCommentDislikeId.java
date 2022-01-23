package keeper.project.homepage.entity.identifier;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import keeper.project.homepage.entity.CommentEntity;
import keeper.project.homepage.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MemberHasCommentDislikeId implements Serializable {

  @ManyToOne(targetEntity = MemberEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private MemberEntity memberEntity;
  @ManyToOne(targetEntity = CommentEntity.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private CommentEntity commentEntity;
}
