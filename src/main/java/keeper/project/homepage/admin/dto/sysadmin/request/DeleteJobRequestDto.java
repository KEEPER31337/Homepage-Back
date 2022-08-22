package keeper.project.homepage.admin.dto.sysadmin.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteJobRequestDto {

  @NotNull
  private Long jobId;
}
