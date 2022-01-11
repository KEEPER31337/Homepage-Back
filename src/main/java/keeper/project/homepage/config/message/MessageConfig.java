package keeper.project.homepage.config.message;


import keeper.project.homepage.service.ResponseService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageConfig {

  ResponseService responseService;


  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("exception");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
