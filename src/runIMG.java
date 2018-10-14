import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class runIMG {

    public static void main(String[] args) {


        String input = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\b2";

        String output1 = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\b1 " +getTime();
        String output2 = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\b1_c" +getTime();
        String img_type ="JPG";


  /*      String input = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\MK";

        String output1 = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\MK" +getTime();
        String output2 = "C:\\Users\\vk\\OneDrive\\courses\\2018\\2018H2\\181019\\IMG2FIG\\res\\MK_c" +getTime();
        String img_type ="JPEG";
*/
        /**
         * 创建对象时，也可以不设定参数，有默认参数
         * bg_alw为-1说明是无背景模式
         * **/
        Img2Fig i2f = new Img2Fig();
        Color color = new Color(145,186,182);
        i2f.setBackground(Color.WHITE,50);
        i2f.setFigure(5,10,20);
        i2f.setFont("宋体",10,Color.BLACK);


        /**
         * 首先进行图片读取和裁剪，参数设置要在此之前
         * 返回BufferImage对象用于后面的图片操作
         * **/
        //BufferedImage bi = i2f.imgRead("D:\\MK", "JPEG");
        BufferedImage bi = i2f.imgRead(input, img_type);
        /**
         * 根据图片获取图片的像素组
         * **/
        int[][] img = i2f.getImgFig(bi);

        /**
         * 绘制，保存
         * **/
        BufferedImage bi_new = i2f.drawByImgFig(img);
        //i2f.getCutImg(bi_new);
        i2f.saveImg(bi_new,img_type,output1);

        /**
         * 根据原图和绘制出的图片，对符号进行上色，笔刷默认为黑色
         * **/
        BufferedImage bi_c = i2f.paintImgFigureColors(bi,bi_new,Color.BLACK);
        i2f.saveImg(bi_c,img_type,output2);

    }
    private static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");//设置日期格式
        String date = df.format(new Date());
        return date;
    }

}
