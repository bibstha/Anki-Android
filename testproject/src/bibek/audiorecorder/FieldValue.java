package bibek.audiorecorder;

import java.io.Serializable;

/**
 * @author Bibek Shrestha <bibekshrestha@gmail.com>
 */
public class FieldValue implements Serializable
{
    private static final long serialVersionUID = 6129938975946291029L;
    private boolean isTemporary = false;
    private String filePath;
    private String tmpPath;
    private String description;

    /**
     * Identifies if a file needs to be deleted after copying
     * 
     * @return true if the file needs to be deleted. false means do not delete
     *         the file
     */
    public boolean isTemporary()
    {
        return isTemporary;
    }

    /**
     * @param isTemporary
     *            set true if this file needs to be deleted after copying, false
     *            if no delete necessary. Default value is false.
     */
    public void setTemporary(boolean isTemporary)
    {
        this.isTemporary = isTemporary;
    }

    /**
     * @return The path where the media is stored inside the anki folder
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * Set the path or file name by which this file will be stored
     * 
     * @param filePath
     */
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    /**
     * @return the name of the existing file name, temporary or in the file
     *         system. The filepath is used to copy the file to the anki
     *         directory
     */
    public String getTmpPath()
    {
        return tmpPath;
    }

    /**
     * @param tmpPath
     *            The path from where the file will be copied
     */
    public void setTmpPath(String tmpPath)
    {
        this.tmpPath = tmpPath;
    }

    /**
     * Any description of the file. Can be used for simple text entries instead
     * of a media
     * 
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     *            The description regarding this audio file
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
}
