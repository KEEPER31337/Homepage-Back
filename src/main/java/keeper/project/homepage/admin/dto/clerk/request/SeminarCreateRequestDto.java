package keeper.project.homepage.admin.dto.clerk.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeminarCreateRequestDto {

  @NotNull
  @JsonSerialize
  @JsonDeserialize
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime openTime;
}
