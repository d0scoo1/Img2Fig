import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class runAV {

    public static void main(String args[]){
        System.out.println("开始视频转换");
        long start = System.currentTimeMillis();

        AV2Fig av2Fig = new AV2Fig();

        String avfile = "C:\\Users\\vk\\Desktop\\0.mp4";
        String ouput = "C:\\Users\\vk\\Desktop\\"+getTime()+".mp4";


        Img2Fig i2f = new Img2Fig();
        i2f.setBackground(Color.WHITE,80);
        i2f.setFigure(20,30,200);
        i2f.setFont("Britannic Bold",30,Color.BLACK);

        try {
            av2Fig.av2FigAV(avfile, ouput, i2f);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("转换时间："+(System.currentTimeMillis() - start)/1000.0);
    }

    private static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");//设置日期格式
        String date = df.format(new Date());
        return date;
    }

}

