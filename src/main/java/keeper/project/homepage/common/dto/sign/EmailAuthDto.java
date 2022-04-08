package keeper.project.homepage.common.dto.sign;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthDto {

  private String emailAddress;
  private String authCode;
}
