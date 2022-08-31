package keeper.project.homepage.user.dto.clerk.response;

import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.user.service.clerk.AttendanceCheckOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckResponseDto {

  @NonNull
  private Boolean isCorrect;
  @Nullable
  private String attendanceStatus;
  @Nullable
  private Integer totalDemerit;
  @Nullable
  private Integer demerit;

  public static AttendanceCheckResponseDto from(SeminarAttendanceEntity seminarAttendanceEntity,
      Boolean isCorrectCode, Integer demerit) {
    return AttendanceCheckResponseDto.builder()
        .attendanceStatus(seminarAttendanceEntity.getSeminarAttendanceStatusEntity().getType())
        .isCorrect(isCorrectCode)
        .totalDemerit(seminarAttendanceEntity.getMemberEntity().getDemerit())
        .demerit(demerit)
        .build();
  }

  public static AttendanceCheckResponseDto getInstance(AttendanceCheckOption notCorrect) {
    return AttendanceCheckResponseDto.builder()
        .isCorrect(notCorrect.getIsCorrect())
        .attendanceStatus(notCorrect.getAttendanceStatus())
        .totalDemerit(notCorrect.getTotalDemerit())
        .demerit(notCorrect.getDemerit())
        .build();
  }
}
