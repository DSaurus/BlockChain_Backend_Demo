package bcapp;

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



@WebServlet(urlPatterns = {"/api/access"})
public class Access extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        String url = request.getRequestURL().toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        JSONObject res = new JSONObject();
        //search block chain to find history

        res.put("code", "501");
        res.put("message", "no implementation");
        out.print(res);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {

        String client_id = request.getParameter("client_id");
        String reason = request.getParameter("reason");

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

        //smart contract ...
        boolean is_accepted = false;

        //if accepted
        if(is_accepted){
            try {
                Connection c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/testdb",
                                StaticConfig.db_user, StaticConfig.db_pass);
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("select * from clients where id = " + client_id + ";");
                ResultSetMetaData rsmd = rs.getMetaData();
                if(rs.next()){
                    int n = rsmd.getColumnCount();
                    res.put("code", "200");
                    for(int i = 1; i <= n; i++){
                        String column_name = rsmd.getColumnName(i);
                        res.put(column_name, rs.getObject(column_name));
                    }
                    out.write(res.toJSONString());
                    return;
                }
                res.put("code", "404");
                res.put("message", "client not found");
                out.write(res.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                res.put("code", "500");
                res.put("message", "database error");
                out.write(res.toJSONString());
            }
        } else {
            res.put("code", "401");
            res.put("message", "You are not allowed to see it!");
        }
    }

}
