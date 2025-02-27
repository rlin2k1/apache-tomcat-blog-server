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

import postsdatabase.PostsDatabase;
import postslist.PostsList;
import poststitlebody.PostsTitleBody;
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
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException ex)
        {
            System.err.println(ex);
            return;
        }
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");

        try
        {
            PostsDatabase pd = new PostsDatabase("jdbc:mysql://localhost:3306/CS144", "cs144", "");

            switch(action==null?"":action) {  // If action is null, set as empty string so default can catch it
                case "open":
                    openHandler(request, response, pd); // Return the "edit page" for the post with the given postid by the user
                    break;
                case "preview":
                    previewHandler(request, response); // Return the "preview page" with the html rendering of the given title and body
                    break;
                case "list":
                    listHandler(request, response, pd); // Return the "list page" for the user
                    break;
                default:
                    request.setAttribute("errorReason", "Action Parameter can only be one of open, preview or list via GET");
                    errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch (NumberFormatException nfe)
        {
            NumberFormatExceptionHandler(request, response, nfe);
        }
        catch (SQLException ex)
        {
            SQLExceptionHandler(request, response, ex);
        }
    }
    
    /**
     * Handles HTTP POST requests
     * 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // POST Request parameters will not show up in the URL
        String action = request.getParameter("action");

        try 
        {
            PostsDatabase pd = new PostsDatabase("jdbc:mysql://localhost:3306/CS144", "cs144", "");
            
            switch(action==null?"":action) { 
                case "open":
                    openHandler(request, response, pd); // Return the "edit page" for the post with the given postid by the user
                    break;
                case "save":
                    saveHandler(request, response, pd); // Save the post into the database and go to the "list page" for the user
                    break; 
                case "delete":
                    deleteHandler(request, response, pd); // Delete the corresponding post and go to the "list page"
                    break; 
                case "preview":
                    previewHandler(request, response); // Return the "preview page" with the html rendering of the given title and body
                    break;
                case "list":
                    listHandler(request, response, pd); // Return the "list page" for the user
                    break;
                default:
                    request.setAttribute("errorReason", "Action Parameter can only be one of open, save, delete, preview or list via POST.");
                    errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch (NumberFormatException nfe)
        {
            NumberFormatExceptionHandler(request, response, nfe);
        }
        catch (SQLException ex)
        {
            SQLExceptionHandler(request, response, ex);
        }
    }

    private void openHandler(HttpServletRequest request, HttpServletResponse response, PostsDatabase pd) throws ServletException, IOException, NumberFormatException, SQLException {
        if (request.getParameter("username") == null || request.getParameter("postid") == null)
        {
            request.setAttribute("errorReason", "Open Action requires username and postid parameters.");
            errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
        }
        else
        {
            if(Integer.parseInt(request.getParameter("postid")) > 0)
            {
                if (request.getParameter("title") != null && request.getParameter("body") != null)
                {
                    request.setAttribute("title", request.getParameter("title"));
                    request.setAttribute("body", request.getParameter("body"));

                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                }
                else
                {
                    PostsTitleBody ptb = pd.getPostsTitleBody(request.getParameter("username"), Integer.parseInt(request.getParameter("postid")));
                    if (ptb != null && ptb.title != null && ptb.body != null)
                    {
                        String title = ptb.title;
                        String body = ptb.body;
                        request.setAttribute("title", title);
                        request.setAttribute("body", body);

                        request.getRequestDispatcher("/edit.jsp").forward(request, response);
                    }
                    else
                    {
                        request.setAttribute("errorReason", "Could not find username and postid combination in the database.");
                        errorHandler(request, response, HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
            else
            {
                if (request.getParameter("title") != null)
                {
                    request.setAttribute("title", request.getParameter("title"));
                }
                else
                {
                    request.setAttribute("title", "");
                }
                if (request.getParameter("body") != null)
                {
                    request.setAttribute("body", request.getParameter("body"));
                }
                else
                {
                    request.setAttribute("body", "");
                }

                request.getRequestDispatcher("/edit.jsp").forward(request, response);
            }
        }
    }

    private void previewHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (request.getParameter("username") == null || request.getParameter("postid") == null || request.getParameter("title")== null || request.getParameter("body") == null)
        {
            request.setAttribute("errorReason", "Preview Action requires username, postid, title, and body parameters.");
            errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
        }
        else
        {
            MarkdownParser mp = new MarkdownParser();
            String markdownTitle = mp.convertToMarkdown(request.getParameter("title"));
            String markdownBody = mp.convertToMarkdown(request.getParameter("body"));
            request.setAttribute("title", markdownTitle);
            request.setAttribute("body", markdownBody);

            request.getRequestDispatcher("/preview.jsp").forward(request, response);
        }
    }

    private void listHandler(HttpServletRequest request, HttpServletResponse response, PostsDatabase pd) throws ServletException, IOException, SQLException
    {
        if (request.getParameter("username") == null)
        {
            request.setAttribute("errorReason", "List Action requires username parameter.");
            errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
        }
        PostsList pl = pd.getPosts(request.getParameter("username"));
        request = listRequest(request, pl);

        request.getRequestDispatcher("/list.jsp").forward(request, response);
    }

    private void saveHandler(HttpServletRequest request, HttpServletResponse response, PostsDatabase pd) throws ServletException, IOException, NumberFormatException, SQLException
    {
        if (request.getParameter("username") == null || request.getParameter("postid") == null || request.getParameter("title")== null || request.getParameter("body") == null)
        {
            request.setAttribute("errorReason", "Save Action requires username, postid, title, and body parameters.");
            errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
        }
        else
        {
            int postid = Integer.parseInt(request.getParameter("postid"));
            pd.savePost(request.getParameter("username"), postid, request.getParameter("title"), request.getParameter("body"));
            PostsList pl = pd.getPosts(request.getParameter("username"));
            request = listRequest(request, pl);

            request.getRequestDispatcher("/list.jsp").forward(request, response);
        }
    }

    private void deleteHandler(HttpServletRequest request, HttpServletResponse response, PostsDatabase pd) throws ServletException, IOException, NumberFormatException, SQLException
    {
        if (request.getParameter("username") == null || request.getParameter("postid") == null)
        {
            request.setAttribute("errorReason", "Delete Action requires username and postid parameters.");
            errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
        }
        else 
        {
            pd.deletePost(request.getParameter("username"), Integer.parseInt(request.getParameter("postid")));
            PostsList pl = pd.getPosts(request.getParameter("username"));
            request = listRequest(request, pl);

            request.getRequestDispatcher("/list.jsp").forward(request, response);
        }
    }

    private void NumberFormatExceptionHandler(HttpServletRequest request, HttpServletResponse response, NumberFormatException nfe) throws ServletException, IOException
    {
        String msg = "NumberFormatException: " + nfe.getMessage() + " | postid needs to be a valid Integer type.";
        System.err.println(msg);
        request.setAttribute("errorReason", msg);
        errorHandler(request, response, HttpServletResponse.SC_BAD_REQUEST);
    }

    private void SQLExceptionHandler(HttpServletRequest request, HttpServletResponse response, SQLException ex) throws ServletException, IOException
    {
        String msg = "SQLException Caught";
        if ( ex != null )
        {
            msg = "SQLException Caught: " + ex.getMessage() + " | SQLState: " + ex.getSQLState() + " | ErrorCode: " + ex.getErrorCode();
        }
        System.err.println(msg);
        request.setAttribute("errorReason", msg);
        errorHandler(request, response, HttpServletResponse.SC_NOT_FOUND);
    }

    private void errorHandler(HttpServletRequest request, HttpServletResponse response, int statusCode) throws ServletException, IOException
    {
        response.setStatus(statusCode);
        
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private HttpServletRequest listRequest(HttpServletRequest request, PostsList pl)
    {
        request.setAttribute("postidList", pl.postidList);
        request.setAttribute("titleList", pl.titleList);
        request.setAttribute("createdTimeList", pl.createdTimeList);
        request.setAttribute("modifiedTimeList", pl.modifiedTimeList);
        request.setAttribute("size", pl.size);

        return request;
    }
};
