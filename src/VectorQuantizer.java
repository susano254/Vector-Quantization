import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class VectorQuantizer{
    int[][] x;
    int n = 2;
    ReadWriteExample reader = new ReadWriteExample();
    //ArrayList<Vector> list;

    VectorQuantizer(){}
    VectorQuantizer(String fileName){
        x = reader.readImage(fileName);
    }

    public void compress(int y[][], int n){
        this.n = n;
        ArrayList<Vector> list =  partition(y, 2);
        ArrayList<Vector> codeBooks = new ArrayList<>();
        ArrayList<Vector> oldPivots = new ArrayList<>();
        ArrayList<Vector> pivots = new ArrayList<>();

        //init the arrayLists initially to the partitioned list
        ArrayList<ArrayList<Vector>> arrayLists = new ArrayList<>();
        arrayLists.add(list);

        //while old pivots not equal new pivots repeat
        while(true) {
            //2- calculate average
            pivots = new ArrayList<>();
            averageAll(arrayLists, pivots);
            if(oldPivots.equals(pivots)) break;

            //3-split the values
            if(codeBooks.size() < 4) {
                codeBooks = new ArrayList<>();
                split(codeBooks, pivots);
            }
            else codeBooks = pivots;

            //compare and associate
            arrayLists =  associate(list, codeBooks);
            oldPivots = pivots;
        }
    }

    public void averageAll(ArrayList<ArrayList<Vector>> arrayLists, ArrayList<Vector> pivots){
        for (int i = 0; i < arrayLists.size(); i++) {
            Vector pivot = average(arrayLists.get(i), n);
            pivots.add(pivot);
        }
    }

    public void split(ArrayList<Vector> codeBooks, ArrayList<Vector> pivots){
        for (int i = 0; i < pivots.size(); i++) {
            ArrayList<Vector> leftAndRight = pivots.get(i).split();
            codeBooks.addAll(leftAndRight);
        }
    }

    public ArrayList<ArrayList<Vector>> associate(ArrayList<Vector> list, ArrayList<Vector> codeBooks){
        HashMap<Vector, ArrayList<Vector>> codeMap = new HashMap<>();
        for (int i = 0; i < codeBooks.size(); i++)
            codeMap.put(codeBooks.get(i), new ArrayList<>());
        for (int i = 0; i < list.size(); i++) {
            Vector current = list.get(i);
            Vector keyVector = compare(current, codeBooks, n);
            codeMap.get(keyVector).add(current);
        }
        return new ArrayList<>(codeMap.values());
    }



    //divide a given image into n*n vectors
    public ArrayList partition(int pixels[][], int n){
        ArrayList<Vector> list = new ArrayList<>();
        int width, height;
        height = pixels.length;
        width = pixels[0].length;

        int listWidth = width/n;
        //divide the y photo into n vectors each of size n*n
        int vectorsSize = (width*height)/(n*n);
        for(int vectorNumber = 0; vectorNumber < vectorsSize; vectorNumber++) {
            //(vectorNumber/listWidth)*vectorHeight; gives the start value of i
            int rowValue = (vectorNumber/listWidth)*n;
            Vector temp = new Vector(n, n);
            for (int i = 0; i < n; i++) {
                //(vectorNumber%listWidth)*vectorHeight; gives the start value of j
                int columnValue = (vectorNumber%listWidth)*n;
                for (int j = 0; j < n; j++) {
                    temp.vector[i][j] = pixels[rowValue][columnValue];
                    //list[vectorNumber][i][j] = y[rowValue][columnValue];
                    columnValue++;
                }
                rowValue++;
            }
            list.add(temp);
        }

        return list;
    }

    //return the average vector for the given list of vectors
    public Vector average(ArrayList<Vector> list, int n) {
        Vector avg = new Vector(n, n);
        //for every vector in the list accumulate its corresponding entries in the "avg" vector
        for (int i = 0; i < list.size(); i++) {
            for (int l = 0; l < n; l++) {
                for (int w = 0; w < n; w++) {
                    avg.vector[l][w] += list.get(i).vector[l][w];
                }
            }
        }
        //finally divide them all with the size
        if(list.size() != 0) {
            for (int l = 0; l < n; l++) {
                for (int w = 0; w < n; w++)
                    avg.vector[l][w] /= list.size();
            }
        }

        //return the averaged vector
        return avg;
    }

    //vector distance pair
    class VectorNode{
        public Vector vector;
        public double distance = 0;
        VectorNode(Vector vector){ this.vector = vector; }
    }

    //returns the best fit codebook for the current vector
    public Vector compare(Vector current, ArrayList<Vector> codeList, int n){
        //a list of vectorNodes(vector, distance) pair for every codebook there is an associated distance
        ArrayList<VectorNode> list = new ArrayList<>(codeList.size());
        //initialize the list with codeBooks in the codeList and distance = 0;
        for(int i = 0; i < codeList.size(); i++)
            list.add(new VectorNode(codeList.get(i)));

        //for every element in the codeList measure its distance from the current vector
        for(int i = 0; i < codeList.size(); i++) {
            for (int l = 0; l < n; l++) {
                for (int w = 0; w < n; w++) {
                    //calculate the difference
                    double temp = codeList.get(i).vector[l][w] - current.vector[l][w];
                    //square the difference
                    list.get(i).distance += temp*temp;
                }
            }
        }
        //finally just find the codebook with the min distance
        //sort according to the distance
        Collections.sort(list, new Comparator<VectorNode>() {
            @Override
            public int compare(VectorNode vectorNode, VectorNode t1) {
                if(vectorNode.distance == t1.distance) return 0;
                else if(vectorNode.distance > t1.distance) return 1;
                else return -1;
            }
        });

        //list is sorted from the smallest distance up so just return the first element
        return list.get(0).vector;

    }
}
