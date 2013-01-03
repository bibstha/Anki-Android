package bibek.audiorecorder;

import java.io.Serializable;

public class PickedMedia implements Serializable
{
    /**
     * A randomly generated serialVersionUID
     */
    private static final long serialVersionUID = 3014926778475980929L;
    private String oldPath;
    private String newPath;
    private STATUS status = STATUS.UNCHANGED;

    public static enum STATUS
    {
        CHANGED, UNCHANGED, DELETED
    }

    public String getOldPath()
    {
        return oldPath;
    }

    public void setOldPath(String oldPath)
    {
        this.oldPath = oldPath;
    }

    public String getNewPath()
    {
        return newPath;
    }

    public void setNewPath(String newPath)
    {
        this.newPath = newPath;
    }

    public STATUS getStatus()
    {
        return status;
    }

    public void setStatus(STATUS status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return this.getOldPath() + "<" + getStatus() + ">" + this.getNewPath();
    }
}
