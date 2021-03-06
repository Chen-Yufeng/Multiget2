package db;

import java.util.List;

import stl.ThreadInfo;

/**
 * Created by If Chan on 2017/10/4.
 */

public interface ThreadDAO {
    public void insertThread(ThreadInfo threadInfo);
    public void deleteThread(String url);
    public void updateThread(String url,int thread_id,long finished);
    public List<ThreadInfo>getThreads(String url);
    public boolean isExists(String url,int thread_id);
}
