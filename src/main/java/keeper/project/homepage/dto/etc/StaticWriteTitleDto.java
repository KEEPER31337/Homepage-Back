package keeper.project.homepage.dto.etc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.CategoryEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.util.annotation.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticWriteTitleDto {

  private String title;
  // type은 Path로 들어오기 때문에 Request Body로 들어 올 필요가 없다.
  @JsonProperty(access = Access.READ_ONLY)
  private String type;

  public StaticWriteTitleEntity toEntity() {

    return StaticWriteTitleEntity.builder()
        .title(title)
        .type(type)
        .build();
  }
}
