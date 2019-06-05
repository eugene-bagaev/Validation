package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.Rules.Results;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        SpringApplication.run(Application.class, args);
    }
}
