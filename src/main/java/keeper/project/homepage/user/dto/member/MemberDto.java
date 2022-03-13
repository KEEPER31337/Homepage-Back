package keeper.project.homepage.user.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.common.controller.util.ImageController;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.util.EnvironmentProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MemberDto {

  private Long id;
  private String loginId;
  private String emailAddress;
  private String password;
  private String realName;
  private String nickName;
  private String authCode;
  private Date birthday;
  private String studentId;
  private Date registerDate;
  private String followeeLoginId;
  private Integer point;
  private Integer level;
  private String rank;
  private String type;
  private List<String> jobs;
  private String thumbnailPath;
  private Integer merit;
  private Integer demerit;
  private Float generation;

  public MemberEntity toEntity() {
    return MemberEntity.builder()
        .loginId(loginId)
        .password(password)
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .build();
  }

  public void initWithEntity(MemberEntity memberEntity) {
    // 민감한 정보 제외
    this.id = memberEntity.getId();
    this.loginId = null;//memberEntity.getLoginId();
    this.password = null;//memberEntity.getPassword();
    this.realName = null;//memberEntity.getRealName();
    this.nickName = memberEntity.getNickName();
    this.birthday = memberEntity.getBirthday();
    this.emailAddress = memberEntity.getEmailAddress();
    this.studentId = null;//memberEntity.getStudentId();
    this.registerDate = memberEntity.getRegisterDate();
    this.point = memberEntity.getPoint();
    this.level = memberEntity.getLevel();
    this.merit = memberEntity.getMerit();
    this.demerit = memberEntity.getDemerit();
    this.generation = memberEntity.getGeneration();

    if (memberEntity.getThumbnail() != null) {
      this.thumbnailPath = EnvironmentProperty.getThumbnailPath(
          memberEntity.getThumbnail().getId());
    }
    if (memberEntity.getMemberRank() != null) {
      this.rank = memberEntity.getMemberRank().getName();
    }
    if (memberEntity.getMemberType() != null) {
      this.type = memberEntity.getMemberType().getName();
    }
    if (memberEntity.getMemberJobs() != null || memberEntity.getMemberJobs().isEmpty() == false) {
      this.jobs = new ArrayList<>();
      memberEntity.getMemberJobs()
          .forEach(job ->
              this.jobs.add(job.getMemberJobEntity().getName()));
    }
  }

}

