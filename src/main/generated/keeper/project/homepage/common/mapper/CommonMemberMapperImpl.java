package keeper.project.homepage.common.mapper;

import javax.annotation.processing.Generated;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberEntity.MemberEntityBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-12T10:42:03+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class CommonMemberMapperImpl extends CommonMemberMapper {

    @Override
    public CommonMemberDto toDto(MemberEntity memberEntity) {
        if ( memberEntity == null ) {
            return null;
        }

        CommonMemberDto commonMemberDto = new CommonMemberDto();

        commonMemberDto.setId( memberEntity.getId() );
        commonMemberDto.setNickName( memberEntity.getNickName() );
        commonMemberDto.setGeneration( memberEntity.getGeneration() );

        commonMemberDto.setJobs( memberEntity.getJobs() );
        commonMemberDto.setThumbnailPath( memberEntity.getThumbnailPath() );

        return commonMemberDto;
    }

    @Override
    public MemberEntity toEntity(CommonMemberDto memberDto) {
        if ( memberDto == null ) {
            return null;
        }

        MemberEntityBuilder memberEntity = MemberEntity.builder();

        memberEntity.id( memberDto.getId() );
        memberEntity.nickName( memberDto.getNickName() );
        memberEntity.generation( memberDto.getGeneration() );

        return memberEntity.build();
    }
}
