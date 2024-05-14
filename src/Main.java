public class Main {
    public static void main(String[] args) {
        //构造实例
        SeamCarvingGUI seamCarvingGUI = new SeamCarvingGUI();
        //启动
        seamCarvingGUI.setVisible(true);

        //制作一个计时器，在处理好图片后输出
        long s=System.currentTimeMillis();
        long e=System.currentTimeMillis();
        System.out.println(e-s);

    }
}
