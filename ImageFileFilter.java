
import java.io.File;
import java.io.FileFilter;
public class ImageFileFilter implements FileFilter
{
    private final String okFileExtensions = "jpg";

    public boolean accept(File file)
    {
        if (file.getName().toLowerCase().endsWith(okFileExtensions) || file.isDirectory())
        {
            return true;
        }
        return false;
    }
}

