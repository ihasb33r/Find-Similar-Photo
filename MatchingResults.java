import java.util.ArrayList;

public class MatchingResults {

    public ArrayList<PhotoResult> results;
    public int size;
    public double worstMatch;

    public MatchingResults(int size){

        results = new ArrayList<PhotoResult>();
        this.size = size;
        this.worstMatch = 0.0;

    }

    public void add(String path, double similarity){

        if (results.isEmpty()){
            results.add(new PhotoResult(path,path,similarity));
        }
        else{
            int i =0;
            while(i<results.size()){
                if(results.get(i).similarity < similarity) {
                    break;
                }
                i++;
            }
            if (i == results.size()){
                results.add(new PhotoResult(path,path, similarity));
            }
            else{
                results.add(i, new PhotoResult(path,path, similarity));
            }
            if (results.size() > size){
                results.remove(results.size()-1);
                worstMatch = results.get(results.size()-1).similarity;
            }
        }
        System.gc();
    }
}

