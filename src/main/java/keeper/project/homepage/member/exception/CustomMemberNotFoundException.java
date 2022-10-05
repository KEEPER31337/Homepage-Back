package keeper.project.homepage.member.exception;

import lombok.Getter;

@Getter
public class CustomMemberNotFoundException extends RuntimeException {

  private Long notFountMemberId;

  public CustomMemberNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomMemberNotFoundException(String msg) {
    super(msg);
  }

  public CustomMemberNotFoundException(Long notFountMemberId) {
    super();
    this.notFountMemberId = notFountMemberId;
  }
}