package keeper.project.homepage.dto;

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
  // DEFAULT 1
  private MemberTypeEntity memberType;
  // DEFAULT 1
  private MemberRankEntity memberRank;
  private int point;
  private int level;
  // DEFAULT 1
  private ThumbnailEntity thumbnail;

  public MemberEntity toEntity() {
    return MemberEntity.builder().loginId(loginId).password(password).realName(realName)
        .nickName(nickName).emailAddress(emailAddress).studentId(studentId)
        .roles(new ArrayList<String>(List.of("ROLE_USER"))).build();
  }

}
