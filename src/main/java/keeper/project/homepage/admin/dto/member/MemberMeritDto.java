package keeper.project.homepage.admin.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberMeritDto {

  private String memberLoginId;
  private Integer merit;
}
