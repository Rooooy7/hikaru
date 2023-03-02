package com.floating.hikaru.module.system;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ErpCodeGenerateUtil {

    public static String businessTemplate = "package {package}.business;\n" +
            "\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "import com.bosum.base.framework.interfaces.IBaseModelOperatorDao;\n" +
            "import com.bosum.base.framework.interfaces.IBaseModelOperatorServiceImpl;\n" +
            "import {package}.dao.{upperPrefix}Dao;\n" +
            "import {package}.model.{upperPrefix}Model;\n" +
            "import {package}.service.{upperPrefix}Service;\n" +
            "\n" +
            "/**\n" +
            " * @author {author}\n" +
            " * @ClassName {upperPrefix}Business.java\n" +
            " * @Description TODO\n" +
            " * @createTime {createTime}\n" +
            " */\n" +
            "@Service(\"/{prefix}Service\")\n" +
            "public class {upperPrefix}Business extends IBaseModelOperatorServiceImpl<{upperPrefix}Model> implements {upperPrefix}Service {\n" +
            "\n" +
            "    @Autowired\n" +
            "    private {upperPrefix}Dao {prefix}Dao;\n" +
            "\n" +
            "    @Override\n" +
            "    public IBaseModelOperatorDao<{upperPrefix}Model> getModelDao() {\n" +
            "        return {prefix}Dao;\n" +
            "    }\n" +
            "}\n";
    public static String serviceTemplate = "package {package}.service;\n" +
            "\n" +
            "import com.bosum.base.framework.interfaces.IBaseModelOperatorService;\n" +
            "import {package}.model.{upperPrefix}Model;\n" +
            "\n" +
            "/**\n" +
            " * @author {author}\n" +
            " * @ClassName {upperPrefix}Service.java\n" +
            " * @Description TODO\n" +
            " * @createTime {createTime}\n" +
            " */\n" +
            "public interface {upperPrefix}Service extends IBaseModelOperatorService<{upperPrefix}Model> {\n" +
            "\n" +
            "}";
    public static String daoTemplate = "package {package}.dao;\n" +
            "\n" +
            "import com.bosum.base.framework.interfaces.IBaseModelOperatorDao;\n" +
            "import {package}.model.{upperPrefix}Model;\n" +
            "\n" +
            "/**\n" +
            " * @author {author}\n" +
            " * @ClassName {upperPrefix}Dao.java\n" +
            " * @Description TODO\n" +
            " * @createTime {createTime}\n" +
            " */\n" +
            "public interface {upperPrefix}Dao extends IBaseModelOperatorDao<{upperPrefix}Model> {\n" +
            "\n" +
            "}";
    public static String daoImplTemplate = "package {package}.dao.impl;\n" +
            "\n" +
            "import com.bosum.base.framework.interfaces.IBaseModelOperatorDaoImpl;\n" +
            "import {package}.dao.{upperPrefix}Dao;\n" +
            "import {package}.model.{upperPrefix}Model;\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "/**\n" +
            " * @author {author}\n" +
            " * @ClassName {upperPrefix}Service.java\n" +
            " * @Description TODO\n" +
            " * @createTime {createTime}\n" +
            " */\n" +
            "@Repository(\"{prefix}Dao\")\n" +
            "public class {upperPrefix}DaoImpl extends IBaseModelOperatorDaoImpl<{upperPrefix}Model> implements {upperPrefix}Dao {\n" +
            "\n" +
            "}";

    public static String modelTemplate = "package {package}.model;\n" +
            "\n" +
            "import com.bosum.base.anno.TableAlias;\n" +
            "import com.bosum.base.anno.TableId;\n" +
            "import lombok.Data;\n" +
            "\n" +
            "import javax.persistence.Table;\n" +
            "import java.io.Serializable;\n" +
            "\n" +
            "/**\n" +
            " * @author {author}\n" +
            " * @ClassName {upperPrefix}Model.java\n" +
            " * @Description TODO\n" +
            " * @createTime {createTime}\n" +
            " */\n" +
            "@TableId(\"{tableId}\")\n" +
            "@TableAlias(\"{tableAlias}\")\n" +
            "@Table(name = \"{tableName}\")\n" +
            "@Data\n" +
            "public class {upperPrefix}Model implements Serializable {\n" +
            "{field}\n" +
            "}";
    public static String fieldTemplate = "    /**\n" +
            "     * {comment}\n" +
            "     */\n" +
            "    private {type} {name};";

    public static Map<String, String> templateMap = new HashMap<>();

    public static Map<String, Pair<String, String>> fieldMap = new LinkedHashMap<>();

    public static StringBuilder globalError = new StringBuilder();

    static {
        templateMap.put("business", businessTemplate);
        templateMap.put("service", serviceTemplate);
        templateMap.put("dao", daoTemplate);
        templateMap.put("dao.impl", daoImplTemplate);
        templateMap.put("model", modelTemplate);
    }

    public static void main(String[] args) throws IOException {

        // 创建 JFrame 实例
        JFrame frame = new JFrame("ERP代码生成工具");
        // Setting the width and height of frame
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);

    }

    private static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        JLabel mysqlLabel = new JLabel("mysql:");
        mysqlLabel.setBounds(30, 20, 80, 25);
        panel.add(mysqlLabel);
        JTextField mysqlText = new JTextField("jdbc:mysql://172.16.253.33:3306/bosum_erp_test?useUnicode=true&serverTimezone=GMT%2B8&characterEncoding=UTF-8&useSSL=false");
        mysqlText.setBounds(100, 20, 240, 25);
        panel.add(mysqlText);

        JLabel userLabel = new JLabel("user:");
        userLabel.setBounds(30, 50, 80, 25);
        panel.add(userLabel);
        JTextField userText = new JTextField("root");
        userText.setBounds(100, 50, 240, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("password:");
        passwordLabel.setBounds(30, 80, 80, 25);
        panel.add(passwordLabel);
        JTextField passwordText = new JTextField("123456");
        passwordText.setBounds(100, 80, 240, 25);
        panel.add(passwordText);

        JLabel tableNameLabel = new JLabel("表名:");
        tableNameLabel.setBounds(30, 140, 80, 25);
        panel.add(tableNameLabel);
        JTextField tableNameText = new JTextField();
        tableNameText.setBounds(100, 140, 240, 25);
        panel.add(tableNameText);

        JLabel packageNameLabel = new JLabel("包名:");
        packageNameLabel.setBounds(30, 170, 80, 25);
        panel.add(packageNameLabel);
        JTextField packageNameText = new JTextField("com.bosum.web");
        packageNameText.setToolTipText("例：com.bosum.web.xxx");
        packageNameText.setBounds(100, 170, 240, 25);
        panel.add(packageNameText);

        JLabel authorLabel = new JLabel("作者:");
        authorLabel.setBounds(30, 200, 80, 25);
        panel.add(authorLabel);
        JTextField authorText = new JTextField("");
        authorText.setBounds(100, 200, 240, 25);
        panel.add(authorText);

        // 错误文本域
        JLabel errorLabel = new JLabel("");
        errorLabel.setBounds(140, 280, 300, 25);
        errorLabel.setForeground(Color.RED);

        // 创建代码到桌面按钮
        JButton loginButton = new JButton("创建代码到桌面");
        loginButton.setBounds(90, 230, 180, 25);
        loginButton.addActionListener(event -> {
            errorLabel.setText("");
            errorLabel.setForeground(Color.RED);
            if (tableNameText.getText() == null || tableNameText.getText().length() == 0) {
                errorLabel.setText("表名不能为空！");
                return;
            }
            if (packageNameText.getText() == null || packageNameText.getText().length() == 0) {
                errorLabel.setText("包名不能为空！");
                return;
            }
            if (mysqlText.getText() == null || mysqlText.getText().length() == 0) {
                errorLabel.setText("数据库地址不能为空！");
                return;
            }
            if (userText.getText() == null || userText.getText().length() == 0) {
                errorLabel.setText("数据库账号不能为空！");
                return;
            }
            if (passwordText.getText() == null || passwordText.getText().length() == 0) {
                errorLabel.setText("数据库密码不能为空！");
                return;
            }

            fieldMap.clear();
            globalError = new StringBuilder();
            generateCode(packageNameText.getText(), tableNameText.getText(), mysqlText.getText(), userText.getText(), passwordText.getText(), authorText.getText());
            if (globalError.length() > 0) {
                errorLabel.setText(globalError.toString());
            } else {
                errorLabel.setText("生成代码成功");
                errorLabel.setForeground(Color.GREEN);
            }
        });
        panel.add(loginButton);
        panel.add(errorLabel);


    }

    public static void generateCode(String packageName, String tableName, String mysqlUrl, String user, String pwd, String author) {
        String prefix = lineToHump(tableName);
        String upperPrefix = upperFirstChar(prefix);

        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        StringBuilder baseFolder = new StringBuilder(homeDirectory.getAbsolutePath()).append("\\java");
        for (String pkg : packageName.split("\\.")) {
            baseFolder.append("\\").append(pkg);
        }
        if ("".equals(author)) {
            author = null;
        }

        generateComponentCode(packageName, author, prefix, upperPrefix, baseFolder.toString());
        generateModelCode(packageName, mysqlUrl, user, pwd, tableName, upperPrefix, baseFolder.toString(), author, prefix);
    }

    private static void generateComponentCode(String packageName, String author, String prefix, String upperPrefix, String baseFolder) {
        templateMap.forEach((String name, String template) -> {
            String upperName = upperFirstChar(name);
            if (upperName.contains(".")) {
                int dotIndex = upperName.indexOf(".");
                upperName = upperName.substring(0, dotIndex).concat(upperFirstChar(upperName.substring(dotIndex + 1)));
            }
            String[] split = name.split("\\.");
            StringBuilder dir = new StringBuilder(baseFolder);
            for (String s : split) {
                dir.append("\\").append(s);
            }
            new File(dir.toString()).mkdirs();
            File file = new File(dir + "\\" + upperPrefix + upperName + ".java");
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
                writer.write(replaceFun(template, packageName, prefix, upperPrefix, author));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void generateModelCode(String packageName, String url, String user, String pwd, String tableName, String upperPrefix, String baseFolder, String author, String prefix) {
        String DRIVER_MANAGER = "com.mysql.cj.jdbc.Driver";
        Statement statement = null;
        Connection conn = null;
        try {
            Class.forName(DRIVER_MANAGER);
            conn = DriverManager.getConnection(url, user, pwd);
            statement = conn.createStatement();
            String primaryKey = null;
            ResultSet rs = statement.executeQuery("SELECT * FROM information_schema.columns WHERE  1=1 and table_name = '" + tableName + "' order by ordinal_position;");
            while (rs.next()) {
                String column_name = rs.getString("column_name");
                String column_type = rs.getString("column_type");
                String column_comment = rs.getString("column_comment");
                String column_key = rs.getString("column_key");
                if ("PRI".equals(column_key) && primaryKey == null) {
                    primaryKey = column_name;
                }
                fieldMap.put(lineToHump(column_name), new Pair<>(getColumnType(column_type), column_comment));
            }
            String fieldString = getFieldString();

            File file = new File(baseFolder + "\\model\\" + upperPrefix + "Model.java");

            String template = "";
            List<String> allLines = Files.readAllLines(Paths.get(file.getPath()));
            if (!allLines.isEmpty()) {
                template = allLines.stream().collect(Collectors.joining("\n"));
            }

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
                String modelString = template.replace("{tableId}", primaryKey).replace("{tableAlias}", getTableAlias(tableName))
                        .replace("{tableName}", tableName).replace("{field}", fieldString);
                writer.write(modelString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getColumnType(String type) {
        String columnType = null;
        int index = type.indexOf("(");
        if (index != -1) {
            type = type.substring(0, index);
        }
        switch (type.toUpperCase()) {
            case "BIT":
            case "INTEGER":
            case "TINYINT":
            case "SMALLINT":
            case "NUMERIC":
            case "INT":
                columnType = "Integer";
                break;
            case "BIGINT":
                columnType = "Long";
                break;
            case "DECIMAL":
                columnType = "BigDecimal";
                break;
            case "BOOLEAN":
                columnType = "Boolean";
                break;
            case "FLOAT":
            case "REAL":
                columnType = "Float";
                break;
            case "DOUBLE":
                columnType = "Double";
                break;
            case "VARCHAR":
            case "NVARCHAR":
            case "CHAR":
            case "NCHAR":
            case "TEXT":
                columnType = "String";
                break;
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                columnType = "Date";
                break;
            case "VARBINARY":
                columnType = "Bytes";
                break;
            case "NULL":
                break;
            case "LONGVARBINARY":
                break;
            default:
        }
        return columnType;
    }

    private static String getTableAlias(String tableName) {
        String name = tableName.toLowerCase();
        StringBuilder alias = new StringBuilder();
        for (String segment : name.split("_")) {
            alias.append(segment.substring(0, 1));
        }
        return alias.toString();
    }

    private static String getFieldString() {
        StringBuilder sb = new StringBuilder();
        fieldMap.forEach((String columnName, Pair<String, String> typeComment) -> {
            if (typeComment.getValue() != null) {
                sb.append(fieldTemplate.replace("{comment}", typeComment.getValue()).replace("{type}", typeComment.getKey()).replace("{name}", columnName));
                sb.append("\n");
            }
        });
        return sb.toString();
    }

    public static String upperFirstChar(String str) {
        return new StringBuilder(str.substring(0, 1).toUpperCase()).append(str.substring(1)).toString();
    }

    public static String replaceFun(String template, String packageName, String prefix, String upperPrefix, String author) {
        String createTime = convertDate2Str(new Date(), "yyyy年MM月dd日");
        String result = template.replace("{package}", packageName).replace("{prefix}", prefix).replace("{upperPrefix}", upperPrefix)
                .replace("{author}", Optional.ofNullable(author).orElse("TODO")).replace("{createTime}", createTime);
        return result;
    }

    public static String convertDate2Str(Date date, String format)
    {
        if (null == date)
        {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 下划线转驼峰
     */
    static public String lineToHump(String str) {
        str = str.toLowerCase();
        Pattern linePattern = Pattern.compile("_(\\w)");
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
