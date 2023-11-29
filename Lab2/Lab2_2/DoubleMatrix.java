package Napredno.Lab2.Lab2_2;

import java.util.Arrays;
import java.util.Objects;

public  final class DoubleMatrix {
    private double [][]a;
    private final int m;
    private final int n;

    public DoubleMatrix(double[] a, int m, int n) throws InsufficientElementsException {
        this.m = m;
        this.n = n;
        if(n*m>a.length)throw  new InsufficientElementsException("Insufficient number of elements");
        int counter=0;
        if (n*m<a.length) {
            counter=a.length-n*m;
        }
        this.a=new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                this.a[i][j]=a[counter++];
            }
        }

    }
    public int rows(){
        return m;
    }
    public int columns(){
        return n;
    }
    public String getDimensions(){
        return "["+m+" x "+n+"]";
    }
    public double maxElementAtRow(int row)throws InvalidRowNumberException{
        if(row<1 || row>m)throw new InvalidRowNumberException("Invalid row number");
        return Arrays.stream(a[row-1]).max().getAsDouble();
    }
    public double maxElementAtColumn(int column)throws InvalidColumnNumberException{
        if(column <1 || column>n)throw new InvalidColumnNumberException("Invalid column number");

        double max=a[0][column-1];
        for (int i=1;i<m;i++){
            if(a[i][column-1]>max){
                max=a[i][column-1];
            }
        }
        return max;
    }
    public double sum(){
        double sum=0.0;
        for (int i=0;i<m;i++){
            sum+=Arrays.stream(a[i]).sum();
        }
        return sum;
    }
   public double [] toSortedArray(){
        double []result=new double[m*n];
        int counter=0;
        for (int i=0;i<m;i++){
            for (int j=0;j<n;j++){
                result[counter++]=a[i][j];
            }
        }
        Arrays.sort(result);
       for (int i = 0; i < m*n/2; i++) {
           double tmp=result[i];
           result[i]=result[m*n-i-1];
           result[m*n-1-i]=tmp;
       }
       return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleMatrix that = (DoubleMatrix) o;
        return m == that.m && n == that.n && Arrays.deepEquals(a, that.a);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(m, n);
        result = 31 * result + Arrays.deepHashCode(a);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res= new StringBuilder();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                res.append(String.format("%.2f",a[i][j]));
                if(j!=n-1)  res.append("\t");
            }
            if(i!=m-1)res.append("\n");
        }
        return res.toString();
    }
}
