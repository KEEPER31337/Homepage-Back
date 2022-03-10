package keeper.project.homepage.admin.dto.etc;

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

  private String type;

  public StaticWriteTitleEntity toEntity() {

    return StaticWriteTitleEntity.builder()
        .title(title)
        .type(type)
        .build();
  }
}
