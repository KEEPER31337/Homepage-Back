package keeper.project.homepage.entity.ctf;

import java.io.Serializable;
import lombok.Data;

@Data
public class CtfTeamHasMemberEntityPK implements Serializable {

  private Long team;
  private Long member;
}
