// 2개이상의 PK를 가진 memberhaspostinglike, memberhaspostingdislike table을 위한 idclass 정의

package keeper.project.homepage.member.entity;

import java.io.Serializable;
import keeper.project.homepage.posting.entity.PostingEntity;
import lombok.Data;

@Data
public class MemberPostingEntityPK implements Serializable {

  private MemberEntity memberId;

  private PostingEntity postingId;
}
