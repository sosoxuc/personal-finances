package personal.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;

public class DbUtils {

    public static EntityManagerFactory getEmf(Map<?, ?> properties) {
        return Persistence.createEntityManagerFactory("TURNICET", properties);
    }
}
