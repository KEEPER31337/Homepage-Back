package keeper.project.homepage.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExceptionResult {

  private final String statusCode;
  private final String errorMessage;
  private final Object input;

  @Override
  public String toString() {
    return "[" + this.statusCode + "] " + this.errorMessage + " 입력: " + this.input.toString();
  }

}
