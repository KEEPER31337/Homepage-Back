package keeper.project.homepage.repository.library;

import java.util.Optional;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrowEntity, Long> {

  Optional<BookBorrowEntity> findByBookAndMember(BookEntity book, MemberEntity returnMember);
}
