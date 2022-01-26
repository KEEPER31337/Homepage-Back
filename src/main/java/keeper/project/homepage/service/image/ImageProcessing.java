package keeper.project.homepage.service.image;

import java.io.File;
import java.io.IOException;

public interface ImageProcessing {

  void imageProcessing(File image, int width, int height, String fileFormat) throws IOException;
}
