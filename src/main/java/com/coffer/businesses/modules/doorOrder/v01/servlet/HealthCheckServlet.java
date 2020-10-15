package com.coffer.businesses.modules.doorOrder.v01.servlet;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 阿里云健康检测url
 *
 * @author yinkai
 */
public class HealthCheckServlet extends HttpServlet {

    /**
     * 日志对象
     */
    Logger logger = Logger.getLogger(getClass());

    /**
     * 健康检测主方法
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String driver = Global.getConfig("jdbc.driver");
        String url = Global.getConfig("jdbc.url");
        String username = Global.getConfig("jdbc.username");
        String pwd = Global.getConfig("jdbc.password");
        PrintWriter out = resp.getWriter();
        logger.debug("********************阿里云健康检测开始********************");
        // 通过jdbc创建连接检测数据库状态
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, pwd);
            // 健康检测要求1~2分钟请求一次，需要及时释放连接
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            // 连接驱动类不存在，数据库连接失败，释放连接失败，需要返回MD1DB0
            out.append(Constant.HealthStatus.MIDDLEWARE1DB0);
            logger.error("********************检测结果：" + Constant.HealthStatus.MIDDLEWARE1DB0 + "********************");
            return;
        }
        // 数据库状态正常，返回MD1DB1
        out.append(Constant.HealthStatus.MIDDLEWARE1DB1);
        logger.debug("********************检测结果：" + Constant.HealthStatus.MIDDLEWARE1DB1 + "********************");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
