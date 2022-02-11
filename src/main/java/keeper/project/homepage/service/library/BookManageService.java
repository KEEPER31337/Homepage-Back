package keeper.project.homepage.service.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.result.CommonResult;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomAboutFailedException;
import keeper.project.homepage.exception.CustomBookNotFoundException;
import keeper.project.homepage.exception.CustomBookOverTheMaxException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.library.BookBorrowRepository;
import keeper.project.homepage.repository.library.BookRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookManageService {

  private final BookRepository bookRepository;
  private final BookBorrowRepository bookBorrowRepository;
  private final MemberRepository memberRepository;
  private static final Integer MAXIMUM_ALLOWD_BOOK_NUMBER = 4;
  private final ResponseService responseService;

  /**
   * 도서 최대 권수 체크
   */
  public CommonResult doAdd(String title, String author, String information, Long quantity) {

    Long nowTotal = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowTotal = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    }
    Long total = quantity + nowTotal;

    if (quantity + nowTotal > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    }

    addBook(title, author, information, total);
    return responseService.getSuccessResult();
  }

  /**
   * 도서 추가
   */
  public void addBook(String title, String author, String information, Long total) {
    Long borrowState = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      borrowState = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
    }
    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(information)
            .total(total)
            .borrow(borrowState)
            .enable(total)
            .registerDate(new Date())
            .build());
  }

  /**
   * 도서 삭제가 가능한지 체크
   */
  public CommonResult doDelete(String title, String author, Long quantity) {

    if (!bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      throw new CustomBookNotFoundException("책이 존재하지 않습니다.");
    }
    Long numOfBooks = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();
    Long numOfBorrow = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
    if (numOfBooks - quantity == 0 && numOfBorrow == 0) {
      BookEntity bookEntity = bookRepository.findByTitleAndAuthor(title, author).get();
      bookRepository.delete(bookEntity);
    } else if (numOfBooks - quantity < 0) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    } else {
      updateDeleteInformation(title, author, quantity);
    }
    return responseService.getSuccessResult();
  }

  /**
   * 도서 삭제 업데이트
   */
  public void updateDeleteInformation(String title, String author, Long quantity) {
    String information = bookRepository.findByTitleAndAuthor(title, author).get().getInformation();
    Long borrow = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow();
    Long total = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    Long enable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(information)
            .total(total - quantity)
            .borrow(borrow)
            .enable(enable - quantity)
            .registerDate(new Date())
            .build());
  }

  /**
   * 도서 대여 가능 여부 체크
   */
  public CommonResult doBorrow(String title, String author, Long borrowMemberId, Long quantity) {

    Long nowEnable = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowEnable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();
    } else {
      throw new CustomBookNotFoundException("책이 존재하지 않습니다.");
    }

    if (quantity > nowEnable) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    }

    borrowBook(title, author, borrowMemberId, quantity);
    return responseService.getSuccessResult();
  }

  /**
   * 날짜 형 변환
   */
  public String transferFormat(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    String transferDate = format.format(date);

    return transferDate;
  }

  /**
   * 도서 대여
   */
  public void borrowBook(String title, String author, Long borrowMemberId, Long quantity) {
    BookEntity bookId = bookRepository.findByTitleAndAuthor(title, author).get();
    MemberEntity memberId = memberRepository.findById(borrowMemberId).get();
    String borrowDate = transferFormat(new Date());
    String expireDate = getExpireDate(14);

    bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .member(memberId)
            .book(bookId)
            .quantity(quantity)
            .borrowDate(java.sql.Date.valueOf(borrowDate))
            .expireDate(java.sql.Date.valueOf(expireDate))
            .build());

    BookEntity nowBookEntity = bookRepository.findByTitleAndAuthor(title, author).get();
    String infromation = nowBookEntity.getInformation();
    Long total = nowBookEntity.getTotal();
    Long borrow = nowBookEntity.getBorrow() + quantity;
    Long enable = nowBookEntity.getEnable() - quantity;
    Date registerDate = nowBookEntity.getRegisterDate();

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(infromation)
            .total(total)
            .borrow(borrow)
            .enable(enable)
            .registerDate(registerDate)
            .build()
    );
  }

  /**
   * 만료 날짜 구하기
   */
  private String getExpireDate(int date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, date);

    return transferFormat(calendar.getTime());
  }

  /**
   * 연체 도서 구하기
   */
  public List<BookBorrowEntity> sendOverdueBooks(Pageable pageable) {

    java.sql.Date startDate = java.sql.Date.valueOf(getExpireDate(-365));
    java.sql.Date endDate = java.sql.Date.valueOf(getExpireDate(3));

    List<BookBorrowEntity> bookBorrowEntities = bookBorrowRepository.findAllByExpireDateBetween(
        pageable, startDate, endDate);

    return bookBorrowEntities;
  }

  /**
   * 반납 기능 구현
   */
  public CommonResult doReturn(String title, String author, Long returnMemberId, Long quantity) {

    BookEntity book = bookRepository.findByTitleAndAuthor(title, author)
        .orElseThrow(() -> new CustomBookNotFoundException("책이 존재하지 않습니다."));

    MemberEntity member = memberRepository.findById(returnMemberId).get();
    List<BookBorrowEntity> borrowEntity = bookBorrowRepository.findByBookAndMemberOrderByBorrowDateAsc(
        book,
        member);

    if (borrowEntity.isEmpty()) {
      throw new CustomBookNotFoundException("책이 존재하지 않습니다.");
    }

    Long borrowedBook = borrowEntity.get(0).getQuantity();

    if (borrowedBook < quantity) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    }
    if (borrowedBook == quantity) {
      bookBorrowRepository.delete(borrowEntity.get(0));
    } else {
      returnBook(title, author, returnMemberId, quantity);
    }
    return responseService.getSuccessResult();
  }

  private void returnBook(String title, String author, Long returnMemberId, Long quantity) {
    BookEntity book = bookRepository.findByTitleAndAuthor(title, author)
        .orElseThrow(() -> new CustomBookNotFoundException("책이 존재하지 않습니다."));
    MemberEntity member = memberRepository.findById(returnMemberId).orElseThrow(
        CustomMemberNotFoundException::new);

    BookBorrowEntity borrowEntity = bookBorrowRepository.findByBookAndMember(book,
        member).orElseThrow(() -> new CustomBookNotFoundException("책이 존재하지 않습니다."));
    String borrowDate = String.valueOf(
        borrowEntity.getBorrowDate());
    String expireDate = String.valueOf(
        borrowEntity.getExpireDate());
    Long borrowedBook = borrowEntity.getQuantity();

    bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .member(member)
            .book(book)
            .quantity(borrowedBook - quantity)
            .borrowDate(java.sql.Date.valueOf(borrowDate))
            .expireDate(java.sql.Date.valueOf(expireDate))
            .build());

    BookEntity nowBookEntity = bookRepository.findByTitleAndAuthor(title, author).get();
    String infromation = nowBookEntity.getInformation();
    Long total = nowBookEntity.getTotal();
    Long borrow = nowBookEntity.getBorrow() - quantity;
    Long enable = nowBookEntity.getEnable() + quantity;
    Date registerDate = nowBookEntity.getRegisterDate();

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(infromation)
            .total(total)
            .borrow(borrow)
            .enable(enable)
            .registerDate(registerDate)
            .build()
    );
  }
}
