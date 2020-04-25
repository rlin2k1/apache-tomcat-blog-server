import java.io.IOException;
import java.sql.* ;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.sql.*;
import java.util.Calendar;

import postlists.PostLists;
import posttitlebody.PostTitleBody;
import markdownparser.MarkdownParser;
/**
 * Servlet implementation class for Servlet: ConfigurationTest
 *
 */
public class Editor extends HttpServlet {
    /**
     * The Servlet constructor
     * 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public Editor() {}

    public void init() throws ServletException
    {
        /*  write any servlet initialization code here or remove this function */
    }
    
    public void destroy()
    {
        /*  write any servlet cleanup code here or remove this function */
    }

    /**
     * Handles HTTP GET requests
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */

    private void save_post(String username, int postid, String title, String body)
    {
        //Username, PostID, Title, Body, Modified, Created.

        /* load the driver */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            return;
        }
    
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet rs = null;

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
			/* You can think of a JDBC Statement object as a channel
			sitting on a connection, and passing one or more of your
			SQL statements (which you ask it to execute) to the DBMS*/
            if (postid <= 0) { // We want to insert no matter what
                preparedStmt = c.prepareStatement("SELECT postid FROM Posts WHERE username=? ORDER BY postid DESC" );
                preparedStmt.setString(1, username);

                rs = preparedStmt.executeQuery();

                if (rs.next()){
                    postid = rs.getInt("postid") + 1;
                }
                preparedStmt = c.prepareStatement("INSERT INTO Posts(title, body, modified, username, postid, created) VALUES (?, ?, ?, ?, ?, ?)");
            } else { // We want to insert only if it exists
                preparedStmt = c.prepareStatement("UPDATE Posts SET title=?, body=?, modified=? WHERE username=? AND postid=?");
            }

            // create a java timestamp object that represents the current time (i.e., a "current timestamp")
            Calendar calendar = Calendar.getInstance();
            java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());

            // preparedStmt = c.prepareStatement("INSERT INTO Posts VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, body=?, modified=?, postid=?");
            // Insert into the database if the update doesn't exist.

            preparedStmt.setString(4, username);
            preparedStmt.setInt(5, postid);
            preparedStmt.setString(1, title);
            preparedStmt.setString(2, body);
            preparedStmt.setTimestamp(3, ourJavaTimestampObject);
            preparedStmt.setTimestamp(6, ourJavaTimestampObject);

            preparedStmt.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQLException caught");
            System.err.println("---");
            while ( ex != null ) {
                System.err.println("Message   : " + ex.getMessage());
                System.err.println("SQLState  : " + ex.getSQLState());
                System.err.println("ErrorCode : " + ex.getErrorCode());
                System.err.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { preparedStmt.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
        }
    }

    private void delete_post(String username, int postid)
    {
        // We only need Username, PostID since they are Primary Keys

        /* load the driver */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            return;
        }
    
        Connection c = null;
        PreparedStatement preparedStmt = null; 

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
			/* You can think of a JDBC Statement object as a channel
			sitting on a connection, and passing one or more of your
			SQL statements (which you ask it to execute) to the DBMS*/

            preparedStmt = c.prepareStatement("DELETE FROM Posts WHERE username=? AND postid=?" ) ;
            preparedStmt.setString(1, username);
            preparedStmt.setInt(2, postid);

            preparedStmt.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQLException caught");
            System.err.println("---");
            while ( ex != null ) {
                System.err.println("Message   : " + ex.getMessage());
                System.err.println("SQLState  : " + ex.getSQLState());
                System.err.println("ErrorCode : " + ex.getErrorCode());
                System.err.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { preparedStmt.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
        }

    }

    private PostTitleBody get_post_title_body(String username, int postid)
    {
        // We only need Username and Post to Get Single Post of User
        PostTitleBody ptb = null;
        /* load the driver */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            return ptb;
        }
    
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet rs = null;

        String title = null;
        String body = null;

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
			/* You can think of a JDBC Statement object as a channel
			sitting on a connection, and passing one or more of your
			SQL statements (which you ask it to execute) to the DBMS*/

            preparedStmt = c.prepareStatement("SELECT title, body FROM Posts WHERE username=? AND postid=?");
            preparedStmt.setString(1, username);
            preparedStmt.setInt(2, postid);

            rs = preparedStmt.executeQuery();

            if (rs.next()){
                 title = rs.getString("title");
                 body = rs.getString("body");
            }
            ptb = new PostTitleBody(title, body);
        } catch (SQLException ex){
            System.err.println("SQLException caught");
            System.err.println("---");
            while ( ex != null ) {
                System.err.println("Message   : " + ex.getMessage());
                System.err.println("SQLState  : " + ex.getSQLState());
                System.err.println("ErrorCode : " + ex.getErrorCode());
                System.err.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { preparedStmt.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
            return ptb;
        }
    }

    private PostLists get_posts(String username)
    {
        // We only need Username to Get all Posts for User

        // We need to return postid, title, modified time, and created time
        // Int, String, String, String
        PostLists pl = null;
        /* load the driver */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            return pl;
        }
    
        Connection c = null;
        PreparedStatement preparedStmt = null;
        ResultSet rs = null;
        int size = 0;

        ArrayList<String> postidList = new ArrayList<String>();
        ArrayList<String> titleList = new ArrayList<String>();
        ArrayList<String> createdTimeList = new ArrayList<String>();
        ArrayList<String> modifiedTimeList = new ArrayList<String>();

        try {
            /* create an instance of a Connection object */
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", ""); 
			/* You can think of a JDBC Statement object as a channel
			sitting on a connection, and passing one or more of your
			SQL statements (which you ask it to execute) to the DBMS*/

            preparedStmt = c.prepareStatement("SELECT postid, title, created, modified FROM Posts WHERE username=? ORDER BY postid ASC");
            preparedStmt.setString(1, username);

            rs = preparedStmt.executeQuery();

            if (rs != null) 
            {
                rs.last();    // moves cursor to the last row
                size = rs.getRow(); // get row id 
                rs.beforeFirst();
            }

            while (rs.next()){
                 String postid = rs.getString("postid");
                 String title = rs.getString("title");
                 String created = rs.getString("created");
                 String modified = rs.getString("modified");

                 postidList.add(postid);
                 titleList.add(title);
                 createdTimeList.add(created);
                 modifiedTimeList.add(modified);
            }
            pl = new PostLists(postidList, titleList, createdTimeList, modifiedTimeList, size);
        } catch (SQLException ex){
            System.err.println("SQLException caught");
            System.err.println("---");
            while ( ex != null ) {
                System.err.println("Message   : " + ex.getMessage());
                System.err.println("SQLState  : " + ex.getSQLState());
                System.err.println("ErrorCode : " + ex.getErrorCode());
                System.err.println("---");
                ex = ex.getNextException();
            }
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { preparedStmt.close(); } catch (Exception e) { /* ignored */ }
            try { c.close(); } catch (Exception e) { /* ignored */ }
            return pl;
        }
    }

    private HttpServletRequest list_request(HttpServletRequest request, PostLists pl)
    {
        request.setAttribute("postidList", pl.postidList);
        request.setAttribute("titleList", pl.titleList);
        request.setAttribute("createdTimeList", pl.createdTimeList);
        request.setAttribute("modifiedTimeList", pl.modifiedTimeList);
        request.setAttribute("size", pl.size);

        return request;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
	// implement your GET method handling code here
	// currently we simply show the page generated by "edit.jsp"
        String action = request.getParameter("action");

        switch(action==null?"":action)
        { 
            case "open": // return the "edit page" for the post with the given postid by the user
                if (request.getParameter("username") == null || request.getParameter("postid") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    try {
                        if(Integer.parseInt(request.getParameter("postid")) > 0) {
                            if (request.getParameter("title") != null && request.getParameter("body") != null) {
                                request.setAttribute("title", request.getParameter("title"));
                                request.setAttribute("body", request.getParameter("body"));
                                request.getRequestDispatcher("/edit.jsp").forward(request, response);
                            }
                            else {
                                PostTitleBody ptb = get_post_title_body(request.getParameter("username"), Integer.parseInt(request.getParameter("postid")));
                                if (ptb != null && ptb.title != null && ptb.body != null) {
                                    String title = ptb.title;
                                    String body = ptb.body;

                                    request.setAttribute("title", title);
                                    request.setAttribute("body", body);

                                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                                }
                                else {
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                                }
                            }
                        }
                        else {
                            if (request.getParameter("title") != null) {
                                request.setAttribute("title", request.getParameter("title"));
                            }
                            else {
                                request.setAttribute("title", "");
                            }
                            if (request.getParameter("body") != null) {
                                request.setAttribute("body", request.getParameter("body"));
                            }
                            else {
                                request.setAttribute("body", "");
                            }
                            request.getRequestDispatcher("/edit.jsp").forward(request, response);
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.err.println("NumberFormatException: " + nfe.getMessage());
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                }
                break;
            case "preview": // return the "preview page" with the html rendering of the given title and body
                if (request.getParameter("username") == null || request.getParameter("postid") == null || request.getParameter("title")== null || request.getParameter("body") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    MarkdownParser mp = new MarkdownParser();
                    String markdownTitle = mp.convertToMarkdown(request.getParameter("title"));
                    String markdownBody = mp.convertToMarkdown(request.getParameter("body"));

                    request.setAttribute("title", markdownTitle);
                    request.setAttribute("body", markdownBody);

                    request.getRequestDispatcher("/preview.jsp").forward(request, response);
                }
                break;
            case "list": // return the "list page" for the user
                if (request.getParameter("username") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }

                PostLists pl = get_posts(request.getParameter("username"));
                request = list_request(request, pl);

                request.getRequestDispatcher("/list.jsp").forward(request, response);
                break;
            default: // Null Case.
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
        } 
    }
    
    /**
     * Handles HTTP POST requests
     * 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
	// implement your POST method handling code here
	// currently we simply show the page generated by "edit.jsp"
    // POST Requests will not show up in the URL
        String action = request.getParameter("action");

        switch(action==null?"":action)
        { 
            case "open": // return the "edit page" for the post with the given postid by the user
                if (request.getParameter("username") == null || request.getParameter("postid") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    try {
                        if(Integer.parseInt(request.getParameter("postid")) > 0) {
                            if (request.getParameter("title") != null && request.getParameter("body") != null) {
                                request.setAttribute("title", request.getParameter("title"));
                                request.setAttribute("body", request.getParameter("body"));
                                request.getRequestDispatcher("/edit.jsp").forward(request, response);
                            }
                            else {
                                PostTitleBody ptb = get_post_title_body(request.getParameter("username"), Integer.parseInt(request.getParameter("postid")));
                                if (ptb != null && ptb.title != null && ptb.body != null) {
                                    String title = ptb.title;
                                    String body = ptb.body;

                                    request.setAttribute("title", title);
                                    request.setAttribute("body", body);

                                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                                }
                                else {
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                                }
                            }
                        }
                        else {
                            if (request.getParameter("title") != null) {
                                request.setAttribute("title", request.getParameter("title"));
                            }
                            else {
                                request.setAttribute("title", "");
                            }
                            if (request.getParameter("body") != null) {
                                request.setAttribute("body", request.getParameter("body"));
                            }
                            else {
                                request.setAttribute("body", "");
                            }
                            request.getRequestDispatcher("/edit.jsp").forward(request, response);
                        }
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.err.println("NumberFormatException: " + nfe.getMessage());
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                }
                break;
            case "save": // save the post into the database and go to the "list page" for the user
                // We still need to check for parameters.
                if (request.getParameter("username") == null || request.getParameter("postid") == null || request.getParameter("title")== null || request.getParameter("body") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    try {
                        int postid = Integer.parseInt(request.getParameter("postid"));
                        save_post(request.getParameter("username"), postid, request.getParameter("title"), request.getParameter("body"));
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.err.println("NumberFormatException: " + nfe.getMessage());
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }

                    PostLists pl = get_posts(request.getParameter("username"));
                    
                    request = list_request(request, pl);
                    request.getRequestDispatcher("/list.jsp").forward(request, response);
                }
                break; 
            case "delete": // delete the corresponding post and go to the "list page"
                if (request.getParameter("username") == null || request.getParameter("postid") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    try {
                        delete_post(request.getParameter("username"), Integer.parseInt(request.getParameter("postid")));
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.err.println("NumberFormatException: " + nfe.getMessage());
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }

                    PostLists pl = get_posts(request.getParameter("username"));
                    request = list_request(request, pl);

                    request.getRequestDispatcher("/list.jsp").forward(request, response);
                }
                break; 
            case "preview": // return the "preview page" with the html rendering of the given title and body
                if (request.getParameter("username") == null || request.getParameter("postid") == null || request.getParameter("title")== null || request.getParameter("body") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                else {
                    MarkdownParser mp = new MarkdownParser();
                    String markdownTitle = mp.convertToMarkdown(request.getParameter("title"));
                    String markdownBody = mp.convertToMarkdown(request.getParameter("body"));

                    request.setAttribute("title", markdownTitle);
                    request.setAttribute("body", markdownBody);

                    request.getRequestDispatcher("/preview.jsp").forward(request, response);
                }
                break;
            case "list": // return the "list page" for the user
                if (request.getParameter("username") == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }

                PostLists pl = get_posts(request.getParameter("username"));
                request = list_request(request, pl);

                request.getRequestDispatcher("/list.jsp").forward(request, response);
                break;
            default: // Null Case.
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
        } 
    }
}
