package keeper.project.homepage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder                    // builder를 사용할수 있게 합니다.
@Entity                     // jpa entity임을 알립니다.
@Getter                     // user 필드값의 getter를 자동으로 생성합니다.
@NoArgsConstructor          // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor         // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "thumbnail")
public class ThumbnailEntity implements Serializable {

  @Id // pk
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "path", length = 512)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String path;

  @OneToOne
  @JoinColumn(name = "file_id")
  @JsonBackReference
  private FileEntity file;

  @OneToOne(mappedBy = "thumbnail")
  @JsonBackReference(value = "thumbnail")
  private MemberEntity memberEntity;

//  @OneToOne(mappedBy = "thumbnail")
//  @JsonBackReference(value = "posting")
//  private PostingEntity postingEntity;

  @OneToOne(mappedBy = "thumbnail")
  @JsonBackReference(value = "staticWriteSubtitleImage")
  private StaticWriteSubtitleImageEntity staticWriteSubtitleImage;
}
