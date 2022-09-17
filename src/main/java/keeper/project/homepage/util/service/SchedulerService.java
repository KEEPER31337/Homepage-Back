package keeper.project.homepage.util.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

  private final TaskScheduler taskScheduler;

  public void scheduleTask(Runnable task, Date date) {
    taskScheduler.schedule(task, date);
  }

}
