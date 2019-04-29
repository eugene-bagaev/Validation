package main;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HTTPController {
    @PostMapping("/task/online-course/check")
    public ServerResponse start(@RequestBody String body) {
        System.out.println(body);

        ServerResponse response = new ServerResponse(
                "200",
                "Responce Body",
                "SUCCESS"
        );

        return response;
    }

    public class ServerResponse {
        public String body;
        public String status;
        public String message;

        public ServerResponse(String status, String body, String message) {
            this.status = status;
            this.body = body;
            this.message = message;
        }
    }
}
