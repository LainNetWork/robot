package fun.lain.robot;
import fun.lain.robot.service.RobotService;

import lombok.AllArgsConstructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
    }

    @Component
    @AllArgsConstructor
    static class StartUp implements ApplicationRunner {
        private final RobotService robotService;
        @Override
        public void run(ApplicationArguments args) throws Exception {
            robotService.login();
        }
    }

}
