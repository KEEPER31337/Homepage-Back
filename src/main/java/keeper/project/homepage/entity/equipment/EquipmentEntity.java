package keeper.project.homepage.entity.equipment;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.ThumbnailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipment")
public class EquipmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long Id;

  @Column(length = 250, nullable = false)
  String name;

  @Column
  String information;

  @Column(nullable = false)
  Long total;

  @Column(nullable = false)
  Long borrow;

  @Column(nullable = false)
  Long enable;

  @Column(nullable = false)
  LocalDate registerDate;

  @JoinColumn(name = "thumbnail_id")
  @ManyToOne(fetch = FetchType.LAZY)
  ThumbnailEntity thumbnailId;

}
