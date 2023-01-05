import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //ReadWriteExample reader = new ReadWriteExample();
        //int[][] x = reader.readImage("Me.jpg");
        //reader.writeImage(x, "copy");
        int[][] y = { {1, 2, 7, 9, 4, 11}, { 3, 4, 6, 6, 12, 12} , { 4, 9, 15, 14, 9, 9}, {10, 10, 20, 18, 8, 8}, {4, 3, 17, 16, 1 ,4}, {4, 5, 18, 18, 5, 6} };
        int n = 2;

        //1- partition the image into vectors
        VectorQuantizer vq = new VectorQuantizer();
        ArrayList<Vector> codeBooks = new ArrayList<>();
        ArrayList<Vector> list =  vq.partition(y, n);
        ArrayList<ArrayList<Vector>> arrayLists = new ArrayList<>();
        ArrayList<Vector> oldPivots = new ArrayList<>();
        ArrayList<Vector> pivots = new ArrayList<>();
        arrayLists.add(list);

        //while old pivots not equal new pivots repeat
        while(true) {
            //2- calculate average
            pivots = new ArrayList<>();
            vq.averageAll(arrayLists, pivots);
            if(oldPivots.equals(pivots)) break;

            //3-split the values
            if(codeBooks.size() < 4) {
                codeBooks = new ArrayList<>();
                vq.split(codeBooks, pivots);
            }
            else codeBooks = pivots;

            //compare and associate
            arrayLists =  vq.associate(list, codeBooks);
            oldPivots = pivots;
        }


    }
}