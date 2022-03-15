package keeper.project.homepage.entity.study;

import java.io.Serializable;
import lombok.Data;

@Data
public class StudyHasMemberEntityPK implements Serializable {

  private Long member;
  private Long study;
}
