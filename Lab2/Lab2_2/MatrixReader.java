package Napredno.Lab2.Lab2_2;

import java.io.InputStream;
import java.util.Scanner;

public class MatrixReader {
    public static DoubleMatrix read(InputStream input) throws InsufficientElementsException {
        Scanner sc=new Scanner(input);
        int m=sc.nextInt();
        int n=sc.nextInt();
        double []a=new double[m*n];
        for (int i = 0; i < m * n; i++) {
            a[i]=sc.nextDouble();
        }
        return new DoubleMatrix(a,m,n);
    }
}
