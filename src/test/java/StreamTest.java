import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class StreamTest {

  @Test
  public void testOrElseGet() throws Exception {

    var strings = new ArrayList<>(List.of("1", "2"));

    var name = strings
      .stream().filter(s -> s.equals("3"))
      .findAny().orElseGet(() -> {
        var s = "3";
        strings.add(s);
        return s;
      });

    System.out.println("name = " + name);
    System.out.println("strings = " + strings);
  }
}
