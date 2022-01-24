package keeper.project.homepage.entity.member;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberHasMemberJobPK implements Serializable {
    private MemberEntity memberEntity;
    private MemberJobEntity memberJobEntity;
}
