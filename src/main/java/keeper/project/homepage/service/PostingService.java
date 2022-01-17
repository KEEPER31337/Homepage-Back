package keeper.project.homepage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.PostingEntity;
import keeper.project.homepage.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostingService {

  private final PostingRepository postingRepository;

  public List<PostingEntity> findAll(Pageable pageable) {
    return postingRepository.findAll(pageable).getContent();
  }

  public List<PostingEntity> findAllByCategoryId(CategoryEntity categoryEntity, Pageable pageable) {
    return postingRepository.findAllByCategoryId(categoryEntity, pageable);
  }

  public PostingEntity save(PostingEntity postingEntity) {
    return postingRepository.save(postingEntity);
  }

  @Transactional
  public PostingEntity getPostingById(Long pid) {
    return postingRepository.findById(pid).get();
  }

  @Transactional
  public PostingEntity updateById(PostingEntity postingEntity, Long postingId) {
    PostingEntity tempEntity = postingRepository.findById(postingId).get();

    tempEntity.updateInfo(postingEntity.getTitle(), postingEntity.getContent(),
        postingEntity.getUpdateTime(), postingEntity.getIpAddress(),
        postingEntity.getAllowComment(), postingEntity.getIsNotice(), postingEntity.getIsSecret());

    return postingRepository.save(tempEntity);
  }

  @Transactional
  public int deleteById(Long postingId) {
    Optional<PostingEntity> postingEntity = postingRepository.findById(postingId);

    if (postingEntity.isPresent()) {
      postingRepository.delete(postingEntity.get());
      return 1;
    } else {
      return 0;
    }
  }

  @Transactional
  public List<PostingEntity> searchPosting(String type, String keyword,
      CategoryEntity categoryEntity, Pageable pageable) {
    List<PostingEntity> postingEntities = new ArrayList<>();
    switch (type) {
      case "T": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContaining(categoryEntity,
            keyword, pageable);
        break;
      }
      case "C": {
        postingEntities = postingRepository.findAllByCategoryIdAndContentContaining(categoryEntity,
            keyword, pageable);
        break;
      }
      case "TC": {
        postingEntities = postingRepository.findAllByCategoryIdAndTitleContainingOrContentContaining(
            categoryEntity, keyword, keyword, pageable);
        break;
      }
      case "W": {
        /*
         * 멤버 기능 구현 완료시 추가
         * MemberEntity memberEntity = memberRepository.findByNickname??(keyword);
         * postingEntities = postingRepository.findAllByCategoryIdAndMemberId(categoryEntity, memberEntity, pageable);
         */
        break;
      }
    }
    return postingEntities;
  }
}
