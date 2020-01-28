package com.pickmebackend.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static com.pickmebackend.properties.JwtConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class JwtConstantsTest {

    @Test
    void propertyTest() {
        assertEquals(SECRET, "parkdonghyunshinmugoneyangkiseokchoigwangminhasangyeop");
        assertEquals(HEADER, "Authorization");
        assertEquals(TOKEN_VALIDITY, 5 * 60 * 60);
        assertEquals(TOKEN_PREFIX, "Bearer ");
        assertEquals(SERIAL_VERSION_UID, -2550185165626007488L);
    }

}
