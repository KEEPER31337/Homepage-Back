package keeper.project.homepage.util.image.preprocessing;

import java.io.File;

public interface ImageProcessing {

  void imageProcessing(File image, int width, int height, String fileFormat);
}