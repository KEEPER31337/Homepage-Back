package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.CategoryEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.entity.PostingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

  List<PostingEntity> findAllByCategoryId(CategoryEntity category, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndTitleContaining(CategoryEntity category, String title,
      Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndContentContaining(CategoryEntity category,
      String content, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndTitleContainingOrContentContaining(
      CategoryEntity category, String title, String content, Pageable pageable);

  List<PostingEntity> findAllByCategoryIdAndMemberId(CategoryEntity category, MemberEntity member,
      Pageable pageable);
}
