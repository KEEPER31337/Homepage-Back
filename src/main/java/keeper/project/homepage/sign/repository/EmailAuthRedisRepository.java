package keeper.project.homepage.sign.repository;

import keeper.project.homepage.member.entity.EmailAuthRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmailAuthRedisRepository extends CrudRepository<EmailAuthRedisEntity, String> {

}
