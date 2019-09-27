package quotes;

public class Quote {
    String[] tags;
    String author;
    String likes;
    String text;

    @Override
    public String toString(){
        return String.format("The author: %s is quoted:%s",this.author,this.text);
    }
}
