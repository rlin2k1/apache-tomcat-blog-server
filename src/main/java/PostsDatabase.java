package postsdatabase;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.Calendar;

import postslist.PostsList;
import poststitlebody.PostsTitleBody;

public class PostsDatabase
{
    private Connection c = null;
    private PreparedStatement preparedStmt = null;
    private ResultSet rs = null;
    //int size = 0;

    public PostsDatabase(String url, String username, String password) throws SQLException {
        /* Create an instance of a Connection object */
        c = DriverManager.getConnection(url, username, password); 
        /* JDBC Statement object is a channel sitting on a connection, and 
        passing one or more of the SQL statements to the DBMS*/
    }

    public void close() {
        try { rs.close(); } catch (Exception e) { /* ignored */ }
        try { preparedStmt.close(); } catch (Exception e) { /* ignored */ }
        try { c.close(); } catch (Exception e) { /* ignored */ }
    }

    public void savePost(String username, int postid, String title, String body) throws SQLException
    {
        //Username, PostID, Title, Body, Modified, Created.

        // create a java timestamp object that represents the current time (i.e., a "current timestamp")
        Calendar calendar = Calendar.getInstance();
        java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
        
        if (postid <= 0) 
        { // We want to insert no matter what
            preparedStmt = c.prepareStatement("SELECT postid FROM Posts WHERE username=? ORDER BY postid DESC" );
            preparedStmt.setString(1, username);

            rs = preparedStmt.executeQuery();

            if (rs.next())
            {
                postid = rs.getInt("postid") + 1;
            }
            else
            {
                postid = 1;
            }
            preparedStmt = c.prepareStatement("INSERT INTO Posts(title, body, modified, username, postid, created) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStmt.setTimestamp(6, ourJavaTimestampObject);
        } else { // We want to insert only if it exists
            preparedStmt = c.prepareStatement("UPDATE Posts SET title=?, body=?, modified=? WHERE username=? AND postid=?");
        }

        // preparedStmt = c.prepareStatement("INSERT INTO Posts VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, body=?, modified=?, postid=?");
        // Insert into the database if the update doesn't exist.

        preparedStmt.setString(4, username);
        preparedStmt.setInt(5, postid);
        preparedStmt.setString(1, title);
        preparedStmt.setString(2, body);
        preparedStmt.setTimestamp(3, ourJavaTimestampObject);

        preparedStmt.executeUpdate();
    }

    public void deletePost(String username, int postid) throws SQLException
    {
        // We only need Username, PostID since they are Primary Keys
        preparedStmt = c.prepareStatement("DELETE FROM Posts WHERE username=? AND postid=?" ) ;
        preparedStmt.setString(1, username);
        preparedStmt.setInt(2, postid);

        preparedStmt.executeUpdate();
    }

    public PostsTitleBody getPostsTitleBody(String username, int postid) throws SQLException
    {
        // We only need Username and Post to Get Single Post of User
        PostsTitleBody ptb = null;

        String title = null;
        String body = null;

        preparedStmt = c.prepareStatement("SELECT title, body FROM Posts WHERE username=? AND postid=?");
        preparedStmt.setString(1, username);
        preparedStmt.setInt(2, postid);

        rs = preparedStmt.executeQuery();

        if (rs.next()){
            title = rs.getString("title");
            body = rs.getString("body");
            ptb = new PostsTitleBody(title, body);
        }

        return ptb;
    }

    public PostsList getPosts(String username) throws SQLException
    {
        // We only need Username to Get all Posts for User

        // We need to return postid, title, modified time, and created time
        // Int, String, String, String
        PostsList pl = null;

        ArrayList<String> postidList = new ArrayList<String>();
        ArrayList<String> titleList = new ArrayList<String>();
        ArrayList<String> createdTimeList = new ArrayList<String>();
        ArrayList<String> modifiedTimeList = new ArrayList<String>();

        int size = 0;

        preparedStmt = c.prepareStatement("SELECT postid, title, created, modified FROM Posts WHERE username=? ORDER BY postid ASC");
        preparedStmt.setString(1, username);

        rs = preparedStmt.executeQuery();

        if (rs != null) 
        {
            rs.last();    // moves cursor to the last row
            size = rs.getRow(); // get row id 
            rs.beforeFirst();
        } else {
            return pl;
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
        pl = new PostsList(postidList, titleList, createdTimeList, modifiedTimeList, size);
        return pl;
    }
 };
 