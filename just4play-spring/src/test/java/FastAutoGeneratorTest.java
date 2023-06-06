import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FastAutoGeneratorTest {

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    /**
     * 快速生成
     */
    @Test
    public void fastGen() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/just4play?serverTimezone=Asia/Shanghai",
                        "root",
                        "test123456")
                .globalConfig(builder -> {
                    builder.author("Ayuan") // 设置作者
                            .outputDir("/Applications/soft/gpt-key/just4play-spring/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.ayuan") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "/Applications/soft/gpt-key/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user"); // 设置需要生成的表名
                })
                .execute();
    }


    /**
     * 交互生成
     *
     * @param args
     */
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/just4play?serverTimezone=Asia/Shanghai",
                        "root",
                        "test123456")
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称？")).fileOverride())
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();
    }
}
