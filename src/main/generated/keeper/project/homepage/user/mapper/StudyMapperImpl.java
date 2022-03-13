package keeper.project.homepage.user.mapper;

import javax.annotation.processing.Generated;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.entity.study.StudyEntity.StudyEntityBuilder;
import keeper.project.homepage.user.dto.study.StudyDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-13T20:40:23+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class StudyMapperImpl extends StudyMapper {

    @Override
    public StudyDto toDto(StudyEntity studyEntity) {
        if ( studyEntity == null ) {
            return null;
        }

        StudyDto studyDto = new StudyDto();

        studyDto.setId( studyEntity.getId() );
        studyDto.setTitle( studyEntity.getTitle() );
        studyDto.setInformation( studyEntity.getInformation() );
        studyDto.setMemberNumber( studyEntity.getMemberNumber() );
        studyDto.setRegisterTime( studyEntity.getRegisterTime() );
        studyDto.setYear( studyEntity.getYear() );
        studyDto.setSeason( studyEntity.getSeason() );
        studyDto.setGitLink( studyEntity.getGitLink() );
        studyDto.setNoteLink( studyEntity.getNoteLink() );
        studyDto.setEtcLink( studyEntity.getEtcLink() );

        studyDto.setThumbnailPath( studyEntity.getThumbnailPath() );
        studyDto.setMemberList( studyEntity.getStudyMembers() );

        return studyDto;
    }

    @Override
    public StudyEntity toEntity(StudyDto studyDto) {
        if ( studyDto == null ) {
            return null;
        }

        StudyEntityBuilder studyEntity = StudyEntity.builder();

        studyEntity.id( studyDto.getId() );
        studyEntity.title( studyDto.getTitle() );
        studyEntity.information( studyDto.getInformation() );
        studyEntity.memberNumber( studyDto.getMemberNumber() );
        studyEntity.registerTime( studyDto.getRegisterTime() );
        studyEntity.year( studyDto.getYear() );
        studyEntity.season( studyDto.getSeason() );
        studyEntity.gitLink( studyDto.getGitLink() );
        studyEntity.noteLink( studyDto.getNoteLink() );
        studyEntity.etcLink( studyDto.getEtcLink() );

        return studyEntity.build();
    }
}
