import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Run {

    public static void main(String[] args) {

        /**
         * 创建对象时，也可以不设定参数，有默认参数
         * bg_alw为-1说明是无背景模式
         * **/
        Img2Fig i2f =new Img2Fig(Color.WHITE, -1, 40);

        //设置参数
        //i2f.setParam(Color.WHITE,20,30);

        //设定按倍数符号尺寸
        //i2f.setFigureSize(1);

        //按参数设置字符
        //i2f.setFigureSize(5,10,10,40,"宋体",Color.BLACK);

        /**
         * 首先进行图片读取和裁剪，参数设置要在此之前
         * 返回BufferImage对象用于后面的图片操作
         * **/
        BufferedImage bi = i2f.img_cut("D:\\MK",Img2Fig.TYPE_IMG_JPEG);

        /**
         * 根据图片获取图片的像素组
         * **/
        int[][] img = i2f.getImgFig(bi);

        /**
         * 绘制，保存
         * **/
        BufferedImage bi_new = i2f.drawByImgFig(img);
        i2f.saveImg(bi_new,Img2Fig.TYPE_IMG_JPEG,"D:\\MK"+getTime());

        /**
         * 根据原图和绘制出的图片，对符号进行上色，笔刷默认为黑色
         * **/
        BufferedImage bi_c = i2f.paintImgFigureColors(bi,bi_new,Color.BLACK);
        i2f.saveImg(bi_c,Img2Fig.TYPE_IMG_JPEG,"D:\\MK_c"+getTime());

    }
    private static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");//设置日期格式
        String date = df.format(new Date());
        return date;
    }

}
