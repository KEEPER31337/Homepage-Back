package keeper.project.homepage.util.service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

  public void scheduleTask(Runnable task, Date date) {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    TaskScheduler taskScheduler = new ConcurrentTaskScheduler(scheduledExecutorService);
    taskScheduler.schedule(task, date);
  }

}
