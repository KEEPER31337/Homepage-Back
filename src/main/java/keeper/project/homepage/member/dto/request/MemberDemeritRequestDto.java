package keeper.project.homepage.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDemeritRequestDto {

  private String memberLoginId;
  private Integer demerit;
}
