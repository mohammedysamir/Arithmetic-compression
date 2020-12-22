import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Floating{

    public void Compress() throws IOException {
        //clear used variables 
        content="";
        compressed=0.0f;
        low_range=0.0f;
        high_range=1.0f;
        tags.clear();

        //read stream
        Path input=Paths.get(OriginalFile);
        content=ReadFromFile(input);

        //calling function to calculate probability and ranges
        getProbability(content);
        CalcRanges();

        //start to re-calculate ranges
        for(int i=0;i<content.length();i++){
            char current_symbol=content.charAt(i);
            low_range=low_range+(high_range-low_range)*getLower(current_symbol);
            high_range=low_range+(high_range-low_range)*getHigher(current_symbol);
        }
        //get random number in last range to rep. data
        compressed=(high_range+low_range)/2;
        Write(CompressFile);
    }

    public void Decompress() throws IOException {
        FileWriter output=new FileWriter(DecompressFile);
        float code=0.0f;
        low_range=0.0f;
        high_range=1.0f;
        String decomp="";
        char sym=' ';
        for(int i=0;i<content.length();i++){
            code=(compressed-low_range)/(high_range-low_range);
            for(int j=0;j<tags.size();j++){
                //find in which range code lay
                sym=tags.get(j).Symbol;
                if(getLower(sym) < code && code < getHigher(sym)){
                    decomp+=sym;
                    break;
                }
            }
            low_range=low_range+(high_range-low_range)*getLower(sym);
            high_range=low_range+(high_range-low_range)*getHigher(sym);
        }
       output.write(decomp);
       output.close();
    }

    public void isEqual() throws IOException {
        String original=ReadFromFile(Paths.get(OriginalFile));
        String decompressed=ReadFromFile(Paths.get(DecompressFile));
        if(original.equals(decompressed))System.out.println("Equals");
        else System.out.println("Not Equals");
    }


    private void getProbability(String text){
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean isFound = false;
            for (int j = 0; j < tags.size(); j++) {
                if (tags.get(j).Symbol==ch) {
                    // Symbol already in a tag
                    // update probability
                    tags.get(j).Updateprop(content.length());
                    isFound = true;
                    break;
                }
            }
            if (!isFound || tags.isEmpty()) {
                // first time to appear
                Tag new_tag = new Tag();
                new_tag.Symbol = ch;
                new_tag.Updateprop(content.length());
                tags.add(new_tag);
            }
        }
    }

    private void CalcRanges(){
        //iteratre over map to calculate range
        //assign first range form 0 to probability
       tags.get(0).Lower_Range=0;
       tags.get(0).Higher_Range=tags.get(0).probability;
       for(int i=1;i<tags.size();i++){
           tags.get(i).Lower_Range=tags.get(i-1).Higher_Range;
           tags.get(i).Higher_Range=tags.get(i).Lower_Range+tags.get(i).probability;
       }
    }

    private String ReadFromFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8).replace("\n", "").replace("\r", "");
        // remove carrige return and line feed
    }

    private float getLower(char sym){
        for(int i=0;i<tags.size();i++){
            if(tags.get(i).Symbol==sym)return tags.get(i).Lower_Range;
        }
        return -1;
    }

    private float getHigher(char sym){
        for(int i=0;i<tags.size();i++){
            if(tags.get(i).Symbol==sym)return tags.get(i).Higher_Range;
        }
        return -1;
    }

    private void Write(String path) throws IOException {
        String output="";
        FileWriter write=new FileWriter(path);
        for(int i=0;i<tags.size();i++)
            output+="Symbol: "+tags.get(i).Symbol+" probability : "+tags.get(i).probability+System.lineSeparator();
        output+="Compressed value :"+compressed;
        write.write(output);
        write.close();
    }

    private String OriginalFile = "OriginalFile.txt";
    private String CompressFile = "CompressFile.txt";
    private String DecompressFile = "DecompressFile.txt";
    private String content = "";
    private float low_range=0.0f,high_range=1.0f,compressed=0.0f;
    ArrayList<Tag> tags=new ArrayList<Tag>();

}