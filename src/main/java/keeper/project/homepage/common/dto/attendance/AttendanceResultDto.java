package keeper.project.homepage.common.dto.attendance;

import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.common.entity.attendance.AttendanceEntity;
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
public class AttendanceResultDto {

  private Long memberId;
  private String ipAddress;
  private String nickName;
  private ThumbnailEntity thumbnail;
  private String greetings;
  private Integer continuousDay;
  private Integer rank;
  private Integer point;
  private Integer rankPoint;
  private Integer continuousPoint;
  private Integer randomPoint;

  public void initWithEntity(AttendanceEntity attendanceEntity) {
    this.memberId = attendanceEntity.getMember().getId();
    this.ipAddress = hidingIpAddress(attendanceEntity.getIpAddress());
    this.nickName = attendanceEntity.getMember().getNickName();
    this.thumbnail = attendanceEntity.getMember().getThumbnail();
    this.greetings = attendanceEntity.getGreetings();
    this.continuousDay = attendanceEntity.getContinuousDay();
    this.rank = attendanceEntity.getRank();
    this.point = attendanceEntity.getPoint();
    this.rankPoint = attendanceEntity.getRankPoint();
    this.continuousPoint = attendanceEntity.getContinuousPoint();
    this.randomPoint = attendanceEntity.getRandomPoint();
  }

  private String hidingIpAddress(String ipAddress) {
    String[] splits = ipAddress.split("\\.");
    splits[0] = "*";
    splits[1] = "*";

    return String.join(".", splits);
  }
}
