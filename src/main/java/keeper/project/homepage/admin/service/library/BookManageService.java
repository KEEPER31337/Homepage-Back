package keeper.project.homepage.admin.service.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.admin.dto.library.BookDto;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookDepartmentEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.library.CustomBookBorrowNotFoundException;
import keeper.project.homepage.exception.library.CustomBookDepartmentNotFoundException;
import keeper.project.homepage.exception.library.CustomBookNotFoundException;
import keeper.project.homepage.exception.library.CustomBookOverTheMaxException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.library.BookBorrowRepository;
import keeper.project.homepage.repository.library.BookDepartmentRepository;
import keeper.project.homepage.repository.library.BookRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookManageService {

  private final BookRepository bookRepository;
  private final BookBorrowRepository bookBorrowRepository;
  private final MemberRepository memberRepository;
  private static final Integer MAXIMUM_ALLOWD_BOOK_NUMBER = 4;
  private final ResponseService responseService;
  private final BookDepartmentRepository bookDepartmentRepository;

  /**
   * 도서 최대 권수 체크
   */
  public CommonResult doAdd(BookDto bookDto, ThumbnailEntity thumbnailEntity) {

    String title = bookDto.getTitle();
    String author = bookDto.getAuthor();
    String information = bookDto.getInformation();
    Long quantity = bookDto.getQuantity();
    BookDepartmentEntity department = bookDepartmentRepository.findById(bookDto.getDepartment())
        .orElseThrow(() -> new CustomBookDepartmentNotFoundException());

    if (information == null) {
      information = "도서 정보입니다.";
    }

    Long nowTotal = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowTotal = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    }
    Long total = quantity + nowTotal;

    if (total > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    }

    addBook(title, author, information, quantity, department, thumbnailEntity);

    return responseService.getSuccessResult();
  }

  /**
   * 도서 추가
   */
  public void addBook(String title, String author, String information, Long quantity,
      BookDepartmentEntity department, ThumbnailEntity thumbnailId) {

    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      BookEntity updateBookEntity = bookRepository.findByTitleAndAuthor(title, author).get();

      Long nowTotal = updateBookEntity.getTotal();
      Long nowEnable = updateBookEntity.getEnable();

      updateBookEntity.setTotal(nowTotal + quantity);
      updateBookEntity.setEnable(nowEnable + quantity);

      bookRepository.save(updateBookEntity);

    } else {

      bookRepository.save(
          BookEntity.builder()
              .title(title)
              .author(author)
              .information(information)
              .department(department)
              .total(quantity)
              .borrow(0L)
              .enable(quantity)
              .registerDate(new Date())
              .thumbnailId(thumbnailId)
              .build());
    }
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

    BookEntity updateBookEntity = bookRepository.findByTitleAndAuthor(title, author)
        .orElseThrow(() -> new CustomBookNotFoundException());
    Long nowTotal = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    Long nowEnable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();

    updateBookEntity.setTotal(nowTotal - quantity);
    updateBookEntity.setEnable(nowEnable - quantity);

    bookRepository.save(updateBookEntity);
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
    BookEntity bookId = bookRepository.findByTitleAndAuthor(title, author)
        .orElseThrow(() -> new CustomBookNotFoundException());
    MemberEntity memberId = memberRepository.findById(borrowMemberId)
        .orElseThrow(() -> new CustomMemberNotFoundException());
    String borrowDate = transferFormat(new Date());
    String expireDate = getExpireDate(14);

    for (int i = 0; i < quantity; i++) {
      bookBorrowRepository.save(
          BookBorrowEntity.builder()
              .member(memberId)
              .book(bookId)
              .quantity(1L)
              .borrowDate(java.sql.Date.valueOf(borrowDate))
              .expireDate(java.sql.Date.valueOf(expireDate))
              .build());
    }

    BookEntity nowBookEntity = bookRepository.findByTitleAndAuthor(title, author)
        .orElseThrow(() -> new CustomBookNotFoundException());
    Long nowBorrow = nowBookEntity.getBorrow();
    Long nowEnable = nowBookEntity.getEnable();

    nowBookEntity.setBorrow(nowBorrow + quantity);
    nowBookEntity.setEnable(nowEnable - quantity);

    bookRepository.save(nowBookEntity);
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
    List<BookBorrowEntity> borrowEntities = bookBorrowRepository.findByBookAndMemberOrderByBorrowDateAsc(
        book,
        member);

    if (borrowEntities.isEmpty()) {
      throw new CustomBookBorrowNotFoundException("대출 내역이 존재하지 않습니다.");
    }

    Long borrowedBook = Long.valueOf(borrowEntities.size());

    if (borrowedBook < quantity) {
      throw new CustomBookOverTheMaxException("수량 초과입니다.");
    } else {
      for (int deletedCount = 0; deletedCount < borrowedBook; deletedCount++) {
        bookBorrowRepository.delete(borrowEntities.get(0));
      }

      BookEntity nowBookEntity = bookRepository.findByTitleAndAuthor(title, author)
          .orElseThrow(() -> new CustomBookNotFoundException());
      Long nowBorrow = nowBookEntity.getBorrow();
      Long nowEnable = nowBookEntity.getEnable();

      nowBookEntity.setBorrow(nowBorrow - quantity);
      nowBookEntity.setEnable(nowEnable + quantity);

      bookRepository.save(nowBookEntity);
    }

    return responseService.getSuccessResult();
  }
}
