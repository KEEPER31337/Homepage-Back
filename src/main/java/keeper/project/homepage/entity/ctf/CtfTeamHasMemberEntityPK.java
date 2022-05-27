package keeper.project.homepage.entity.ctf;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtfTeamHasMemberEntityPK implements Serializable {

  private Long team;
  private Long member;
}
