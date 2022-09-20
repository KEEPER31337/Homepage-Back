package keeper.project.homepage.repository.library;

import java.sql.Date;
import java.util.List;
import keeper.project.homepage.library.entity.BookBorrowEntity;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import keeper.project.homepage.library.entity.BookEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrowEntity, Long> {

  List<BookBorrowEntity> findAllByExpireDateBetween(Pageable pageable, Date start, Date end);

  Optional<BookBorrowEntity> findByBookAndMember(BookEntity book, MemberEntity returnMember);

  List<BookBorrowEntity> findByBookAndMemberOrderByBorrowDateAsc(BookEntity book, MemberEntity member);

  List<BookBorrowEntity> findByMember(MemberEntity memberEntity);
}
