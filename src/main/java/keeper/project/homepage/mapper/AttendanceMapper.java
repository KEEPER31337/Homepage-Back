package keeper.project.homepage.mapper;

import keeper.project.homepage.dto.attendance.AttendanceDto;
import keeper.project.homepage.entity.attendance.AttendanceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper extends
    GenericMapper<AttendanceDto, AttendanceEntity> {


}
