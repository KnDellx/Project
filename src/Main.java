import edu.princeton.cs.algs4.Picture;

public class Main {
    public static void main(String[] args) {
        long s=System.currentTimeMillis();
        Picture picture =new Picture("/Users/lll/Desktop/project/Project/doc/processing/origin.jpg") ;
        SeamCarver seamCarver =new SeamCarver(picture);
       // seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
     //  seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());

        seamCarver.seamHorizontal(1000);
        seamCarver.picture().show();
        long e=System.currentTimeMillis();
        System.out.println(e-s
        );
       // picture.show();
    }
}
