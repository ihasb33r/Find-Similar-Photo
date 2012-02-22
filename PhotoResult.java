
import org.imgscalr.Scalr;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;


public class PhotoResult{

    private BufferedImage th;
    public String icon, title;
    public double similarity;

    public PhotoResult(){
    }

    public PhotoResult(String i, String t, double s){
        this.icon = i;
        this.title = t;
        this.similarity = s;
    }

    public ImageIcon getImage(){
        BufferedImage ic = null;
        if (th == null){
            try{
                th= Scalr.resize(ImageIO.read(new File(icon)),
                        Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
                        150, 150);
            }
            catch(Exception e){
            }
        }
        return new ImageIcon(th);

    }
}

