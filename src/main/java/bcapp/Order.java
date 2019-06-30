package bcapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.StaticConfig;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Iterator;
import java.util.Set;

@WebServlet(urlPatterns = {"/api/orders/myself",  "/api/orders/list", "/api/orders/evaluate"})
public class Order extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        String url = request.getRequestURL().toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        JSONObject res = new JSONObject();

        if(request.getSession() == null || request.getSession().getAttribute(("user_id")) == null){
            res.put("code", "401");
            res.put("message", "You have not been logged in");
            out.write(res.toJSONString());
            return;
        }

        if(url.contains("myself")){
            Connection c = null;
            try {
                c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:" + StaticConfig.port + "/" + StaticConfig.db_name,
                                StaticConfig.db_user, StaticConfig.db_pass);
                Statement s = c.createStatement();
                String user_id = (String) request.getSession().getAttribute(("user_id"));
                ResultSet rs = s.executeQuery("select * from orders where client_id = " + user_id + ";"); //todo possibly not user_Id
                ResultSetMetaData rsmd = rs.getMetaData();
                JSONArray db_query = new JSONArray();
                while(rs.next()){
                    int n = rsmd.getColumnCount();
                    JSONObject o = new JSONObject();
                    for(int i = 1; i <= n; i++){
                        o.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
                    }
                    db_query.add(o);
                }
                res.put("code", "200");
                res.put("data", db_query.toJSONString());
                out.write(res.toJSONString());
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                res.put("code", "500");
                res.put("message", "database error");
                out.write(res.toJSONString());
                return;
            }
        } else {
            res.put("code", 501);
            res.put("message", "no implementation!");
        }
        out.print(res);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {

        String url = request.getRequestURL().toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        JSONObject res = new JSONObject();

        if(request.getSession() == null || request.getSession().getAttribute(("user_id")) == null){
            res.put("code", "401");
            res.put("message", "You have not been logged in");
            out.write(res.toJSONString());
            return;
        }

        if(url.contains("evaluate")){
            String user_id = (String) request.getSession().getAttribute("user_id");
            String order_id = request.getParameter("order_id");
            JSONObject data = (JSONObject) JSON.parse(request.getParameter("data"));
            try {
                Connection c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:" + StaticConfig.port + "/" + StaticConfig.db_name,
                                StaticConfig.db_user, StaticConfig.db_pass);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("select * from orders where client_id = " + user_id + " and id = " + order_id + ";"); //todo User_id?
                if(rs.next()){
                    Iterator<String> it = data.keySet().iterator();
                    String data_sql = "status = 1, ";
                    while(it.hasNext()){
                        String key = it.next();
                        data_sql += " " + key + "=" + "\'" + data.getString(key) + "\', ";
                    }
                    s.executeUpdate("update orders_status set " + data_sql + " where id = " + order_id + ";");
                    res.put("code", "200");
                    res.put("message", "evaluate successfully");
                    out.write(res.toJSONString());
                    return;
                } else {
                    res.put("code", "401");
                    res.put("message", "You are not allowed to evaluate this order");
                    out.write(res.toJSONString());
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                res.put("code", "500");
                res.put("message", "database error");
                out.write(res.toJSONString());
                return;
            }
        } else {
            String user_id = (String) request.getSession().getAttribute("user_id");
            String order_id = request.getParameter("order_id");
            try {
                Connection c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:" + StaticConfig.port + "/" + StaticConfig.db_name,
                                StaticConfig.db_user, StaticConfig.db_pass);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("select * from orders where client_id = " + user_id + " and id = " + order_id + ";"); //todo User_id?
                if(rs.next()){
                    ResultSetMetaData rsmd = rs.getMetaData();
                    JSONArray db_query = new JSONArray();
                    while(rs.next()){
                        int n = rsmd.getColumnCount();
                        JSONObject o = new JSONObject();
                        for(int i = 1; i <= n; i++){
                            o.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
                        }
                        db_query.add(o);
                    }
                    res.put("code", "200");
                    res.put("data", db_query.toJSONString());
                    out.write(res.toJSONString());
                    return;
                } else {
                    res.put("code", "401");
                    res.put("message", "You are not allowed to see this order");
                    out.write(res.toJSONString());
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                res.put("code", "500");
                res.put("message", "database error");
                out.write(res.toJSONString());
                return;
            }
        }
    }

}
