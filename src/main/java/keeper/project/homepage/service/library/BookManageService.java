package keeper.project.homepage.service.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.library.BookBorrowRepository;
import keeper.project.homepage.repository.library.BookRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookManageService {

  private final BookRepository bookRepository;
  private final BookBorrowRepository bookBorrowRepository;
  private final MemberRepository memberRepository;
  private static final Integer MAXIMUM_ALLOWD_BOOK_NUMBER = 4;

  @Autowired
  public BookManageService(BookRepository bookRepository,
      MemberRepository memberRepository,
      BookBorrowRepository bookBorrowRepository,
      MemberRepository memberRepository1) {
    this.bookRepository = bookRepository;
    this.bookBorrowRepository = bookBorrowRepository;
    this.memberRepository = memberRepository1;
  }

  /**
   * 도서 최대 권수 체크
   */
  public Long isCanAdd(String title, String author, Long quantity) {

    Long nowTotal = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowTotal = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    }

    if (quantity + nowTotal > MAXIMUM_ALLOWD_BOOK_NUMBER) {
      return -1L;
    }

    return nowTotal + quantity;
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
  public boolean isExist(String title, String author) {

    if (!bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      return false;
    }
    return true;
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
  public Long isCanBorrow(String title, String author, Long quantity) {

    Long nowEnable = 0L;
    if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
      nowEnable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable();
    } else {
      return -2L;
    }

    if (quantity > nowEnable) {
      return -1L;
    }

    return quantity;
  }

  /**
   * 날짜 형 변환
   */
  private String transferFormat(Date date) {
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
    String expireDate = getExpireDate();

    bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .memberId(memberId)
            .bookId(bookId)
            .quantity(quantity)
            .borrowDate(java.sql.Date.valueOf(borrowDate))
            .expireDate(java.sql.Date.valueOf(expireDate))
            .build());

    String infromation = bookRepository.findByTitleAndAuthor(title, author).get().getInformation();
    Long total = bookRepository.findByTitleAndAuthor(title, author).get().getTotal();
    Long borrow = bookRepository.findByTitleAndAuthor(title, author).get().getBorrow() + quantity;
    Long enable = bookRepository.findByTitleAndAuthor(title, author).get().getEnable() - quantity;

    bookRepository.save(
        BookEntity.builder()
            .title(title)
            .author(author)
            .information(infromation)
            .total(total)
            .borrow(borrow)
            .enable(enable)
            .build()
    );
  }

  /**
   * 만료 날짜 구하기
   */
  private String getExpireDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, 14);

    return transferFormat(calendar.getTime());
  }
}
