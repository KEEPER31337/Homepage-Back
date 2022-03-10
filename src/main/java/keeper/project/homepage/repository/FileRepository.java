package keeper.project.homepage.repository;

import java.util.List;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  List<FileEntity> findAllByPostingId(PostingEntity postingId);
}
