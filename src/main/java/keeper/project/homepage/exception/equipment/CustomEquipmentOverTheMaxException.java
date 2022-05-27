package keeper.project.homepage.exception.equipment;

public class CustomEquipmentOverTheMaxException extends RuntimeException {

  public CustomEquipmentOverTheMaxException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomEquipmentOverTheMaxException(String msg) {
    super(msg);
  }

  public CustomEquipmentOverTheMaxException() {
    super();
  }

}
