package keeper.project.homepage.entity.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend")
public class FriendEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "follower")
  private MemberEntity follower;
  @ManyToOne
  @JoinColumn(name = "followee")
  private MemberEntity followee;
  @Column
  private LocalDate registerDate;

}
