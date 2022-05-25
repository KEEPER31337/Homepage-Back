package keeper.project.homepage.exception.equipment;

public class CustomEquipmentEntityNotFoundException extends RuntimeException{

  public CustomEquipmentEntityNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomEquipmentEntityNotFoundException(String msg) {
    super(msg);
  }

  public CustomEquipmentEntityNotFoundException() {
    super();
  }
}
