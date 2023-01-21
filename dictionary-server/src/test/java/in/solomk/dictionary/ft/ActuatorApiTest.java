package in.solomk.dictionary.ft;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ActuatorApiTest extends BaseFuncTest {

    @Test
    void returnsBasicHealthWhenNotAuthenticated() {
        actuatorTestClient.getHealth(false)
                          .expectStatus()
                          .isOk()
                          .expectBody()
                          .json("""
                                        {"status":"UP"}
                                        """);
    }

    @Test
    void returnsDetailedHealthWhenAuthenticated() {
        actuatorTestClient.getHealth(true)
                          .expectStatus()
                          .isOk()
                          .expectBody()
                          .json("""
                                        {"status":"UP"}
                                        """);
    }

    @Test
    @Disabled
    void returnsInfo() {
        actuatorTestClient.getInfo(true)
                          .expectStatus()
                          .isOk()
                          .expectBody()
                          .json("""
                                        {"app":{"name":"dictionary-server","version":"1.0.0-SNAPSHOT"}}
                                        """);
    }
}
