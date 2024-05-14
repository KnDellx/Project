import edu.princeton.cs.algs4.Picture;

public class Main {
    public static void main(String[] args) {
        long s=System.currentTimeMillis();
        Picture picture =new Picture("doc/processing/originx.jpg") ;
        SeamCarver seamCarver =new SeamCarver(picture);
       // seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
     //  seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());

        seamCarver.seamHorizontal(100);
        seamCarver.picture().show();
        long e=System.currentTimeMillis();
        System.out.println(e-s
        );
       // picture.show();
    }
}
