package keeper.project.homepage.about.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDto {

  // TODO: 사용하지 않는 필드이다. 제거하는 것이 좋다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime time;

  // TODO: 사용하지 않는 필드이다. 제거하는 것이 좋다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  private Long memberId;
  // TODO: 요청 시에 IP 주소는 request Header에서 가져오고 있기 때문에 @JsonProperty(access = Access.READ_ONLY)를 달아서
  // TODO: API 호출 시 불필요한 ip address를 보내는 것을 방지한다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  private String ipAddress;
  private String greetings;

  // TODO: 사용하지 않는 필드이다. 제거하는 것이 좋다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private LocalDate startDate;
  // TODO: 사용하지 않는 필드이다. 제거하는 것이 좋다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private LocalDate endDate;

  // TODO: 사용하지 않는 필드이다. 제거하는 것이 좋다.
  // TODO: *API 스펙이 변경되는 것이기 때문에 FE와 협업이 필요하다.*
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private LocalDate date;
}
