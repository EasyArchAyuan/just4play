import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.junit.jupiter.api.Test;

/**
 * @author Ayuan
 * @Description: TODO
 * @date 2023/5/24 12:12
 */
public class MySQLGeneratorTest extends BaseGeneratorTest {
    /**
     * 数据源配置
     */
    public static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://localhost:3306/just4play?serverTimezone=Asia/Shanghai", "root", "test123456")
            .schema("just4play")
            .build();

    @Test
    public void testSimple() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }
}
