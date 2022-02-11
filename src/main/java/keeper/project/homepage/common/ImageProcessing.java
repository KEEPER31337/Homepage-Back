package keeper.project.homepage.common;

import java.io.File;
import java.io.IOException;

public interface ImageProcessing {

  void imageProcessing(File image, int width, int height, String fileFormat);
}
