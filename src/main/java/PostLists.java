package postlists;

import java.util.List;
import java.util.ArrayList;
public class PostLists
{
    public ArrayList<String> postidList;
    public ArrayList<String> titleList;
    public ArrayList<String> createdTimeList;
    public ArrayList<String> modifiedTimeList;
    public int size;

    public PostLists(ArrayList<String> postidList, ArrayList<String> titleList, ArrayList<String> createdTimeList, ArrayList<String> modifiedTimeList, int size)
    {
        this.postidList = postidList;
        this.titleList = titleList;
        this.createdTimeList = createdTimeList;
        this.modifiedTimeList = modifiedTimeList;
        this.size = size;
    }
 };
 