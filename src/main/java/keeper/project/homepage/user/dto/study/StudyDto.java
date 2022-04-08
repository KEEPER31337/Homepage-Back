package keeper.project.homepage.user.dto.study;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.user.dto.member.MemberDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long id;
  private String title;
  private String information;
  @JsonProperty(access = Access.READ_ONLY)
  private Integer memberNumber;
  @JsonProperty(access = Access.READ_ONLY)
  private LocalDateTime registerTime;
  private Integer year;
  private Integer season;
  private String gitLink;
  private String noteLink;
  private String etcLink;
  @JsonProperty(access = Access.READ_ONLY)
  private String thumbnailPath;
  @JsonProperty(access = Access.READ_ONLY)
  private MemberDto headMember;
  @JsonProperty(access = Access.WRITE_ONLY)
  private String ipAddress;
  @JsonProperty(access = Access.READ_ONLY)
  private List<MemberDto> memberList = new ArrayList<>();
}
