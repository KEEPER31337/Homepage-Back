package keeper.project.homepage.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 공통 매핑 정보, 상속 받아서 사용할 수 있도록
@EntityListeners(AuditingEntityListener.class) // 이벤트가 발생할 때 마다 특정 로직, 자동으로 값을 넣어주도록
public abstract class BaseTimeEntity {

  @CreatedDate
  private LocalDateTime registerDate;

//  @LastModifiedDate
//  private LocalDateTime modifiedDate;

}
