package keeper.project.homepage.user.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendancePointDto {

  public static final int FIRST_PLACE_POINT = 500;
  public static final int SECOND_PLACE_POINT = 300;
  public static final int THIRD_PLACE_POINT = 100;
  public static final int WEEK_ATTENDANCE = 7;
  public static final int MONTH_ATTENDANCE = 28;
  public static final int YEAR_ATTENDANCE = 365;
  public static final int DAILY_ATTENDANCE_POINT = 1000;
  public static final int WEEK_ATTENDANCE_POINT = 3000;
  public static final int MONTH_ATTENDANCE_POINT = 10000;
  public static final int YEAR_ATTENDANCE_POINT = 100000;

}
