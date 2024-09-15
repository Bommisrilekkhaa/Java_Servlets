
package todolist;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet("/api/todos")
@SuppressWarnings("serial")
public class TodoServlet extends HttpServlet {
	
	@Override
    public void init() throws ServletException {
        super.init();
        try {
        	
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found", e);
        }
    }
	private Connection connect() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/task_manager";
        String user = "root"; 
        String password = "Buddy@7748"; 
        return DriverManager.getConnection(url, user, password);
    }
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	doOptions(request,response);

            String task = request.getParameter("task");

            if (task == null || task.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                insertTask(task);
                response.setStatus(HttpServletResponse.SC_OK);
            } 
            catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doOptions(request, response);
        try {
            HashMap<Integer, String> tasks = getAllTasks();
            response.setContentType("application/json");

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("["); 
            
            int counter = 0;
            int size = tasks.size();
            
            for (int id : tasks.keySet()) {
                String task = tasks.get(id);
                jsonBuilder.append("{")
                           .append("\"id\":\"")
                           .append(id) 
                           .append("\", \"task\":\"")
                           .append(task.replace("\"", "\\\"")) 
                           .append("\"}");
                if (counter < size - 1) {
                    jsonBuilder.append(",");
                }
                counter++;
            }
            jsonBuilder.append("]"); 
            
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doOptions(request, response);
        

        String idStr = request.getParameter("id");
        String newTask = request.getParameter("newTask");
//        System.out.println(idStr);
//        System.out.println(newTask);

        if (idStr == null || newTask == null || idStr.trim().isEmpty() || newTask.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            updateTask(id, newTask);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doOptions(request, response);
        
        String idStr = request.getParameter("id");
//        System.out.println(idStr);
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            deleteTask(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
        
        private void insertTask(String task) throws SQLException {
            String sql = "INSERT INTO tasks (task) VALUES (?)";
            try (Connection conn = connect(); 
            	PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, task);
                stmt.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace(); 
                }
        }

        private HashMap<Integer,String> getAllTasks() throws SQLException {
        	HashMap<Integer,String> tasks = new HashMap<>();
            String sql = "SELECT * FROM tasks";
            
            try (Connection conn = connect(); 
            		Statement stmt = conn.createStatement(); 
            		ResultSet rs = stmt.executeQuery(sql)) {
        	if(conn!=null) {
//        		System.out.println("sql..........");
        		}
                while (rs.next()) {
//                	System.out.println(rs.next());
                	
                    String task = rs.getString("task");
                    tasks.put(rs.getInt("id"),task);
                }
            }catch (SQLException e) {
            e.printStackTrace(); 
        }
            return tasks;
        }
        
        private void updateTask(int id, String newTask) throws SQLException {
            String sql = "UPDATE tasks SET task = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newTask);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            }
        }

        private void deleteTask(int id) throws SQLException {
            String sql = "DELETE FROM tasks WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
        
        protected void doOptions(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
		  response.setHeader("Access-Control-Allow-Origin", "*");
		  response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		  response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		  response.setHeader("Access-Control-Max-Age", "3600");
	      response.setStatus(HttpServletResponse.SC_OK);
		}

}
	
	
	
	
	
	
	
	