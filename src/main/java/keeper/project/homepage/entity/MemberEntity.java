package keeper.project.homepage.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder                    // builder를 사용할수 있게 합니다.
@Entity                     // jpa entity임을 알립니다.
@Getter                     // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor          // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor         // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member")     // 'member' 테이블과 매핑됨을 명시
public class MemberEntity implements UserDetails {

  @Id // pk
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "login_id", length = 80, nullable = false, unique = true)
  private String loginId;
  @Column(name = "email_address", length = 250, nullable = false, unique = true)
  private String emailAddress;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Json 결과로 출력 안 할 데이터
  @Column(name = "password", length = 512, nullable = false)
  private String password;
  @Column(name = "real_name", length = 40, nullable = false)
  private String realName;
  @Column(name = "nick_name", length = 40, nullable = false)
  private String nickName;
  @Column(name = "birthday")
  private Date birthday;
  @Column(name = "student_id", length = 45, nullable = false, unique = true)
  private String studentId;
  @Column(name = "member_type_id")
  private int memberTypeId;
  @Column(name = "member_rank_id")
  private int memberRankId;
  @Column(name = "point", nullable = false)
  private int point;
  @Column(name = "level", nullable = false)
  private int level;

  @PrePersist // persist 되기 전에 호출되는 함수, 생성할 때 0으로 고정이므로 대입, 입력되는 값이라면 확인이 필요
  public void prePersist() {
    this.memberTypeId = 1;
    this.memberRankId = 1;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @Builder.Default
  private List<String> roles = new ArrayList<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public String getUsername() {
    return this.loginId;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isEnabled() {
    return true;
  }
}