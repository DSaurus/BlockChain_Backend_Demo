package bcapp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

@WebServlet(urlPatterns = {"/api/users/login", "/api/users/logout"})
public class Login extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        String url = request.getRequestURL().toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        JSONObject res = new JSONObject();


        System.out.println("user_id:" + request.getSession().getAttribute("user_id"));

        if(request.getSession() == null){
            res.put("code", 401);
            res.put("message", "session error!");
            out.print(res);
            return;
        }

        if(url.contains("login")){
            if (request.getSession().getAttribute("user_id") != null){
                res.put("code", 200);
                res.put("message", "You have been logged in!");
                res.put("user_id", request.getSession().getAttribute("user_id"));
            } else {
                res.put("code", 401);
                res.put("message", "You have not been logged in!");
            }
        } else if(url.contains("logout")){
            if (request.getSession().getAttribute("user_id") != null){
                res.put("code", 200);
                res.put("message", "log out successfully!");
                request.getSession().removeAttribute("user_id");
            } else {
                res.put("code", 401);
                res.put("message", "You have not been logged in!");
            }
        }
        out.print(res);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, ServletException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        JSONObject res = new JSONObject();
        System.out.println("user_id:" + request.getSession().getAttribute("user_id"));
        Connection c = null;
        Statement stmt = null;
        try {
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/testdb",
                            "postgres", "123456");
            String sql = "SELECT * FROM users WHERE name = \'" + name + "\';";
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                String db_password = rs.getString("password");
                if(db_password.equals(password)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user_id", rs.getInt("id"));
                    res.put("code", "200");
                    res.put("message", "login successful");
                    out.print(res.toJSONString());
                    return;
                }
            }
            res.put("code", "401");
            res.put("message", "password or account is wrong");
            out.print(res.toJSONString());

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            res.put("code", "500");
            res.put("message", "database error!\n" + e.getClass().getName() + ": " + e.getMessage());
            out.print(res.toJSONString());
        }
    }

}
