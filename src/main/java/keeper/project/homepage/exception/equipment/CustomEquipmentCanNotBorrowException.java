package keeper.project.homepage.exception.equipment;

public class CustomEquipmentCanNotBorrowException extends RuntimeException {

  public CustomEquipmentCanNotBorrowException(String msg, Throwable t) {
    super(msg, t);
  }

  public CustomEquipmentCanNotBorrowException(String msg) {
    super(msg);
  }

  public CustomEquipmentCanNotBorrowException() {
    super();
  }

}
