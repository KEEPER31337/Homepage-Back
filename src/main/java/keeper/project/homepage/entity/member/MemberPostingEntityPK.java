// 2개이상의 PK를 가진 memberhaspostinglike, memberhaspostingdislike table을 위한 idclass 정의

package keeper.project.homepage.entity.member;

import java.io.Serializable;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.Data;

@Data
public class MemberPostingEntityPK implements Serializable {

  private MemberEntity memberId;

  private PostingEntity postingId;
}
