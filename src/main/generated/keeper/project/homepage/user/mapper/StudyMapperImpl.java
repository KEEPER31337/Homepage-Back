package keeper.project.homepage.user.mapper;

import javax.annotation.processing.Generated;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberEntity.MemberEntityBuilder;
import keeper.project.homepage.entity.study.StudyEntity;
import keeper.project.homepage.entity.study.StudyEntity.StudyEntityBuilder;
import keeper.project.homepage.user.dto.member.MemberDto;
import keeper.project.homepage.user.dto.study.StudyDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-14T20:50:46+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.1 (Oracle Corporation)"
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
        studyDto.setHeadMember( studyEntity.headMemberToDto() );
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
        studyEntity.headMember( memberDtoToMemberEntity( studyDto.getHeadMember() ) );

        return studyEntity.build();
    }

    protected MemberEntity memberDtoToMemberEntity(MemberDto memberDto) {
        if ( memberDto == null ) {
            return null;
        }

        MemberEntityBuilder memberEntity = MemberEntity.builder();

        memberEntity.id( memberDto.getId() );
        memberEntity.loginId( memberDto.getLoginId() );
        memberEntity.emailAddress( memberDto.getEmailAddress() );
        memberEntity.password( memberDto.getPassword() );
        memberEntity.realName( memberDto.getRealName() );
        memberEntity.nickName( memberDto.getNickName() );
        memberEntity.birthday( memberDto.getBirthday() );
        memberEntity.studentId( memberDto.getStudentId() );
        memberEntity.registerDate( memberDto.getRegisterDate() );
        memberEntity.point( memberDto.getPoint() );
        memberEntity.level( memberDto.getLevel() );
        memberEntity.merit( memberDto.getMerit() );
        memberEntity.demerit( memberDto.getDemerit() );
        memberEntity.generation( memberDto.getGeneration() );

        return memberEntity.build();
    }
}
