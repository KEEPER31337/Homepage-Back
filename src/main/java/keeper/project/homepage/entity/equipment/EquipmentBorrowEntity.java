package keeper.project.homepage.entity.equipment;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipment_borrow_info")
public class EquipmentBorrowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long Id;

  @Column
  Long quantity;

  @Column
  Date borrowDate;

  @Column
  Date expireDate;

  @JoinColumn(name = "member_id")
  @ManyToOne(fetch = FetchType.LAZY)
  MemberEntity memberId;

  @JoinColumn(name = "equipment_id")
  @OneToOne(fetch = FetchType.LAZY)
  EquipmentEntity equipmentId;
}
