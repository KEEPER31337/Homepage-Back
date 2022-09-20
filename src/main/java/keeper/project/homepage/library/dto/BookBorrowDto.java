package keeper.project.homepage.library.dto;

import java.sql.Date;
import keeper.project.homepage.library.entity.BookBorrowEntity;
import keeper.project.homepage.library.entity.BookEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookBorrowDto {

  private Long memberId;
  private Long bookId;
  private Long quantity;
  private Date borrowDate;
  private Date expireDate;

  public BookBorrowEntity toEntity(BookEntity bookEntity, MemberEntity memberEntity) {

    return BookBorrowEntity.builder().member(memberEntity).book(bookEntity).quantity(quantity)
        .borrowDate(borrowDate).expireDate(expireDate).build();
  }


}
