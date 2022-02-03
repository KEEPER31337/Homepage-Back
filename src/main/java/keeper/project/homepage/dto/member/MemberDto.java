package keeper.project.homepage.dto.member;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberRankEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
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
  private Integer point;
  private Integer level;

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
    this.id = memberEntity.getId();
    this.loginId = memberEntity.getLoginId();
    this.password = memberEntity.getPassword();
    this.realName = memberEntity.getRealName();
    this.nickName = memberEntity.getNickName();
    this.emailAddress = memberEntity.getEmailAddress();
    this.studentId = memberEntity.getStudentId();
    this.registerDate = memberEntity.getRegisterDate();
    this.memberType = memberEntity.getMemberType();
    this.memberRank = memberEntity.getMemberRank();
    this.point = memberEntity.getPoint();
    this.level = memberEntity.getLevel();
    this.thumbnail = memberEntity.getThumbnail();
  }
}
