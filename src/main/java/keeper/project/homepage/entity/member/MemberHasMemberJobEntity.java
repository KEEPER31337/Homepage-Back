package keeper.project.homepage.entity.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder                    // builder를 사용할수 있게 합니다.
@Entity                     // jpa entity임을 알립니다.
@Getter                     // user 필드값의 getter를 자동으로 생성합니다.
@Setter
@NoArgsConstructor          // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor         // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member_has_member_job")
@IdClass(MemberHasMemberJobPK.class)
public class MemberHasMemberJobEntity implements Serializable {

  @Id
  @ManyToOne
  @JoinColumn(name = "member_id")
  @JsonBackReference
  private MemberEntity memberEntity;

  @Id
  @ManyToOne
  @JoinColumn(name = "member_job_id")
  @JsonBackReference(value = "member-job-id")
  private MemberJobEntity memberJobEntity;
}
