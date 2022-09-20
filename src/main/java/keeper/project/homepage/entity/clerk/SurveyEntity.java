package keeper.project.homepage.entity.clerk;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "survey")
public class SurveyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime openTime;

  @Column(nullable = false)
  private LocalDateTime closeTime;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 200)
  private String description;

  @Column(nullable = false)
  private Boolean isVisible;

  @Builder.Default
  @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE)
  List<SurveyMemberReplyEntity> respondents = new ArrayList<>();

  public void openSurvey() {
    this.isVisible = true;
  }

  public void closeSurvey() {
    this.isVisible = false;
  }

  public void modifySurveyContents(String name, String description, LocalDateTime openTime,
      LocalDateTime closeTime, Boolean isVisible) {
    this.name = name;
    this.description = description;
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.isVisible = isVisible;
  }
}
