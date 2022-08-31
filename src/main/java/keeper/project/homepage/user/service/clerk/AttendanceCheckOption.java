package keeper.project.homepage.user.service.clerk;

import lombok.Getter;

@Getter
public class AttendanceCheckOption {

  private final Boolean isCorrect = false;
  private final String attendanceStatus = null;
  private final Integer totalDemerit = null;
  private final Integer demerit = null;

  public static AttendanceCheckOption NOT_CORRECT = new AttendanceCheckOption();
}
