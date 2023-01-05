import java.util.ArrayList;

public class Vector {
    public int height, width;
    public double[][] vector;

    Vector(int height, int width){
        this.height = height;
        this.width = width;
        vector = new double[height][width];
    }
    public ArrayList<Vector> split(){
        Vector left = new Vector(2, 2);
        Vector right = new Vector(2, 2);
        for(int l = 0; l < 2; l++){
            for(int w = 0; w < 2; w++) {
                if(this.vector[l][w] % 1 != 0) {
                    left.vector[l][w] = Math.floor(this.vector[l][w]);
                    right.vector[l][w] = Math.ceil(this.vector[l][w]);
                }
                else {
                    left.vector[l][w] = this.vector[l][w] - 1;
                    right.vector[l][w] = this.vector[l][w] + 1;
                }

            }
        }

        ArrayList<Vector> list = new ArrayList<>();
        list.add(left);
        list.add(right);
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        Vector temp = (Vector) obj;
        boolean equal = true;
        for (int l = 0; l < 2 && equal; l++) {
            for (int w = 0; w < 2; w++) {
                if(this.vector[l][w] != temp.vector[l][w]){
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }
}
