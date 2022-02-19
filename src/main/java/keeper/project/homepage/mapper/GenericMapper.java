package keeper.project.homepage.mapper;

//D : Dto, E : Entity
public interface GenericMapper<D, E>{

  D toDto(E e);
  E toEntity(D d);
}
