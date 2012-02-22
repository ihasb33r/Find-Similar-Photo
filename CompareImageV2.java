import  static org.imgscalr.Scalr.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;


public class CompareImageV2{
    public static int WIDTH=512;
    public static int SIZE=WIDTH*WIDTH;
    public static int DESIRED_WIDTH;
    public ArrayList<File> files = new ArrayList<File>();
    public String cacheName = "cache";
    public String tempCache = "tempcache";
    public static long start;
    public static PrintWriter out;

    public CompareImageV2(){

    }

    public void writeFileList(){
        PrintWriter filelist = null;
        try {
            FileWriter outFile = new FileWriter("filelist.log");
            filelist = new PrintWriter(outFile, true);
        } catch (IOException x) {
        }
        if (filelist !=null){
        for (File file : files){
            filelist.println(file.getPath());
        }
        }
    }

/*
    public static void MyUtils.log(Object... messages){
        StringBuilder bld = new StringBuilder(new Date(System.currentTimeMillis()).toString()).append(" : ");
        for(Object obj : messages){
            bld.append(obj);
        }
        out.println(bld);
    }
    */

    public void getFiles(String directory){

        File archiveFile = new File(directory);
        File[] listOfFiles = archiveFile.listFiles(new ImageFileFilter());
        for (File file: listOfFiles){

            if (file.isDirectory()){
                getFiles(file.getPath());
            }
            else{
                files.add(file);
            }
        }
        writeFileList();


    }

    public char[] getHash(String filename){

        MyUtils.log("##### LOG: GET HASH FOR",filename);

        BufferedImage image = null;
        try{
        MyUtils.log("##### LOG: GET IMAGE FOR " , filename);
            image = ImageIO.read(new File(filename));
        MyUtils.log("##### LOG: IMAGE SIZE ", image.getWidth(), "x", image.getHeight());
        }
        catch (Exception e){
        MyUtils.log("##### LOG: FAILED RETURNING BLACK ", filename);
            e.printStackTrace();
            char[] x  = new char[SIZE];
            Arrays.fill(x,'0');
            return x;
        }
        BufferedImage thumb = null;
        if (image != null)
        {
        MyUtils.log("##### LOG: RESIZE FOR HASHING ", filename);
            thumb= resize(image,
                    Method.SPEED, Mode.FIT_EXACT,
                    WIDTH, WIDTH, OP_GRAYSCALE);
        }
        if( WIDTH>DESIRED_WIDTH){
        MyUtils.log("##### LOG: CROP FOR HASHING ", filename);
            thumb = crop(thumb,(WIDTH-DESIRED_WIDTH)/2, (WIDTH-DESIRED_WIDTH)/2, DESIRED_WIDTH, DESIRED_WIDTH);
            /*
            thumb= resize(thumb,
                    Method.SPEED, Mode.FIT_EXACT,
                    DESIRED_WIDTH, DESIRED_WIDTH, OP_GRAYSCALE);
                    */
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream( SIZE );
        int[] argb  =  new int[SIZE];

        thumb.getRGB(0,0,DESIRED_WIDTH,DESIRED_WIDTH,argb,0,DESIRED_WIDTH);
        int total = 0;
        for (int i=0 ; i<SIZE; i++){
//            int alpha = (argb[i] >> 24) & 0xFF;
            int red =   (argb[i] >> 16) & 0xFF;
            total = total + red; 
        }
        double average = total/(double)SIZE;
        char[] calculatedByMean = new char[SIZE] ;
        MyUtils.log("##### LOG: SIMPLIFY ",  filename);
        for (int j=0 ; j<SIZE; j++){
            int red =   (argb[j] >> 16) & 0xFF;
            calculatedByMean[j] =  (red>average)? '1':'0';
        }
        thumb = null;
        image = null;
        baos = null;
        System.gc();
        MyUtils.log("##### LOG: GOT HASH FOR ",  filename);
        return calculatedByMean;
    }

    public double compare(char[] a, char[] b){
        MyUtils.log("##### LOG: COMPARE ");


        int total = 0;
        for (int j =0; j<SIZE ; j ++){
            if (a[j]!= b[j]){
                total = total+1;
            }
        }

        return (((SIZE-total)/(double)SIZE)*100);
    }
    public List findMatches (String requestedPhoto, String archiveDir, int num, String cropPercent, String gridSize){
        int gridWidth = Integer.parseInt(gridSize);
        int cropSize = Integer.parseInt(cropPercent);
        SIZE = gridWidth*gridWidth;
        DESIRED_WIDTH = gridWidth;
        WIDTH = (int)(gridWidth/(1.0-(cropSize/100.0)));
        return findMatches(requestedPhoto, archiveDir, num);
    }

    public List findMatches (String requestedPhoto, String archiveDir, int num){

        char[] requestedPhotoHash = getHash(requestedPhoto);
        getFiles(archiveDir);
        MatchingResults myresults = new MatchingResults(num);
        char[] candidateHash;
        double percent;
        MyUtils.log("##### LOG: GOT ",  files.size(),"FILES");
        for (int j=0 ; j<files.size();j++){
        MyUtils.log("##### LOG: PROCESS ",  j,  " FILE ",  files.get(j).getPath());

            candidateHash = getHash(files.get(j).getPath());
            percent = compare(requestedPhotoHash,candidateHash);
            if (percent >myresults.worstMatch){
                MyUtils.log("##### LOG: GOT BETTER RESULT ");
                myresults.add(files.get(j).getPath(), percent);
            }
            /*
            if(j%num==0){
                System.gc();

            }
            */

        }
//        out.close();
        return myresults.results;

    }
    
    public static void main(String[] args){

        CompareImageV2 comparator = new CompareImageV2();
        comparator.start = System.currentTimeMillis();
//        MyUtils.log(" #############LOG GET INPUT");

        String requestedPhoto = args[0];
        String archiveDir = args[1];

        comparator.findMatches(requestedPhoto, archiveDir, 10);

    }
}
