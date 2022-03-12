package keeper.project.homepage.user.dto.posting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class PostingResponseDto {

  private static final String thumbnailApiPath = "/v1/util/thumbnail/";

  private Long id;
  private String title;
  private String content;
  private String writer;
  private Long writerId;
  private String writerThumbnailPath;
  private Integer size;
  private Integer visitCount;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer commentCount;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;
  private String ipAddress;
  private Integer allowComment;
  private Integer isNotice;
  private Integer isSecret;
  private Integer isTemp;
  private String category;
  private String thumbnailPath;
  private List<FileEntity> files;

  public PostingResponseDto initWithEntity(PostingEntity postingEntity, Integer size) {

    ThumbnailEntity memberThumbnail = postingEntity.getMemberId().getThumbnail();
    ThumbnailEntity postingThumbnail = postingEntity.getThumbnail();

    PostingResponseDto postingResponseDto = PostingResponseDto.builder()
        .id(postingEntity.getId())
        .title(postingEntity.getTitle())
        .content(postingEntity.getContent())
        .writer(postingEntity.getMemberId().getNickName())
        .writerId(postingEntity.getMemberId().getId())
        .writerThumbnailPath(null)
        .size(size)
        .visitCount(postingEntity.getVisitCount())
        .likeCount(postingEntity.getLikeCount())
        .dislikeCount(postingEntity.getDislikeCount())
        .commentCount(postingEntity.getCommentCount())
        .registerTime(postingEntity.getRegisterTime())
        .updateTime(postingEntity.getUpdateTime())
        .ipAddress(postingEntity.getIpAddress())
        .allowComment(postingEntity.getAllowComment())
        .isNotice(postingEntity.getIsNotice())
        .isSecret(postingEntity.getIsSecret())
        .isTemp(postingEntity.getIsTemp())
        .category(postingEntity.getCategoryId().getName())
        .files(postingEntity.getFiles())
        .thumbnailPath(null)
        .build();

    // 썸네일 경로 처리
    if (memberThumbnail != null) {
      postingResponseDto.setWriterThumbnailPath(thumbnailApiPath + memberThumbnail.getId());
    }

    if (postingThumbnail != null) {
      postingResponseDto.setThumbnailPath(thumbnailApiPath + postingThumbnail.getId());
    }

    // 비밀게시판 처리
    if (postingEntity.getCategoryId().getName().equals("비밀게시판")) {
      postingResponseDto.setWriter("익명");
      postingResponseDto.setWriterId(-1L);
      postingResponseDto.setWriterThumbnailPath("");
    }

    // 비밀글 처리
    if (postingEntity.getIsSecret() == 1) {
      postingResponseDto.setContent("비밀 게시글입니다.");
    }

    return postingResponseDto;
  }
}
