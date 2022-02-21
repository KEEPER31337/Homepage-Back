package keeper.project.homepage.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileWrapper implements MultipartFile {

  private final String name;
  private final String originalFileName;
  private final String contentType;
  private final boolean isEmpty;
  private final Long size;
  private final File tmpFileDirPath;
  private File tmpFile;

  public MultipartFileWrapper(MultipartFile request) {
    super();
    this.name = request.getName();
    this.originalFileName = request.getOriginalFilename();
    this.contentType = request.getContentType();
    this.isEmpty = request.isEmpty();
    this.size = request.getSize();

    this.tmpFileDirPath = new File(System.getProperty("java.io.tmpdir"));
    if (this.tmpFileDirPath.exists() == false) {
      this.tmpFileDirPath.mkdir();
    }
    this.tmpFile = null;
    try {
      this.tmpFile = File.createTempFile("temp_", null, this.tmpFileDirPath);
      request.transferTo(tmpFile);
      System.out.println("임시파일 경로: " + tmpFile.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getOriginalFilename() {
    return this.originalFileName;
  }

  @Override
  public String getContentType() {
    return this.contentType;
  }

  @Override
  public boolean isEmpty() {
    return this.isEmpty;
  }

  @Override
  public long getSize() {
    return this.size;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return Files.readAllBytes(this.tmpFile.toPath());
  }

  @Override
  public InputStream getInputStream() throws IOException {

    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.getBytes());
    InputStream inputStream = new InputStream() {
      @Override
      public int read() throws IOException {
        return byteArrayInputStream.read();
      }
    };
    return inputStream;
  }

  @Override
  public void transferTo(File file) {
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      byte[] strToBytes = this.getBytes();
      outputStream.write(strToBytes);
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void transferFinish() {
    try {
      Files.deleteIfExists(this.tmpFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
