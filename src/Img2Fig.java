import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * 图片转换为字符组成的图片
 *
 * @author vickey
 * @version 1.0.0
 */
public class Img2Fig {

   // private String img_path = null;        //图片输入路径
   // private String img_out_path = null;    //图片输出路径
  //  private String img_type = null; //图片格式

    public static final String TYPE_IMG_PNG = "PNG";
    public static final String TYPE_IMG_JPG = "JPG";
    public static final String TYPE_IMG_JPEG = "JPEG";

    private Color bgc = Color.WHITE; //背景色，默认为白色，如果不想填背景色，则选择图片里的比较少见的颜色做背景色，或把容差改为负数

    //背景容差0-255，建议50-100，数值越大，背景判断越广
    private int bg_alw = 20;   //容差大于255时，则全为背景，如果不想有背景色，可以把bg_alw设置为负数

    //figure判定容差
    private int fig_alw = 20;

    private int width;//处理的图片宽度，可能和实际图片不符，多余部分忽略
    private int height; //处理的图片高度，可能和实际图片不符

    //private int minx = 0;//默认像素开始为0，0
    //private int miny = 0;

    //一个字符的像素大小，默认是X5Y10
    private int fig_width = 5;
    private int fig_height = 10;
    private int fig_font = 10; //关联字符大小
    private String font_type = "宋体";
    public Color paint_color = Color.BLACK;

    private int image_type = 5;


    public Img2Fig(){

    }

    public Img2Fig(Color bgc, int bg_alw, int fig_alw) {

        this.bgc = bgc;
        this.bg_alw = bg_alw;
        this.fig_alw = fig_alw;
     }

     public void setParam(Color bgc, int bg_alw, int fig_alw){
         this.bgc = bgc;
         this.bg_alw = bg_alw;
         this.fig_alw = fig_alw;
     }


    public void setFigureSize(int fig_width,int fig_height,int fig_font,int fig_alw,String font_type,Color paint_color){
        this.fig_width = fig_width;
        this.fig_height = fig_height;
        this.fig_font = fig_font;
        this.fig_alw =  fig_alw;
        this.font_type = font_type;
        this.paint_color = paint_color;
    }

    /**
     * 图片进行裁剪，（其实只是并未真正裁剪，只是不读其像素点）
     *
     * @return 返回一个BufferedImage对象
     */
    public BufferedImage imgCut(String img_path,String img_type) {
        img_path = img_path + "."+img_type;

       // this.img_type = img_type;

        File file = new File(img_path);
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //这里默认从X0Y0开始，所以注释掉
        //int minx = bi.getMinX();
        //int miny = bi.getMinY();

        int width = bi.getWidth();
        int height = bi.getHeight();

        //裁剪出多余的部分
        this.width = width - (width % fig_width);
        this.height = height - (height % fig_height);

        this.image_type = bi.getType();

        return bi;
    }

    public BufferedImage imgNotCut(BufferedImage bi) {
        
        this.width = bi.getWidth();
        this.height = bi.getHeight();

        return bi;
    }

    public void setWH(int width,int height){
        this.width = width - (width % fig_width);
        this.height = height - (height % fig_height);
    }

    /**
     * @param bi 图片对象
     * @return 返回二维数组记录每个字符信息，用于生成图片
     */
    public int[][] getImgFig(BufferedImage bi) {

        //开始像素minx,miny默认为0,预留以后更新从非0开始情况，而且这里要注意是用于对像素的循环，不是对像素组的循环
        int minx = 0;
        int miny = 0;

        int f_width = width / fig_width;    //分组后，组的宽度
        int f_height = height / fig_height; //分组后，组的高度

        int img_fig[][] = new int[f_width][f_height]; //记录每个字符信息

        //初始化img_fig,默认值为-1，-1可以用来作为背景标志
        for (int i = 0; i < f_width; i++) {
            for (int j = 0; j < f_height; j++) {
                img_fig[i][j] = -1;
            }
        }

        //针对每个像素组进行判断
        for (int i = minx; i < f_width; i++) {
            for (int j = miny; j < f_height; j++) {

                int c_value = 0; //这个值用于控制显示的符号
                int f_alw = 0;   //对比显示符号的容差，决定是否显示

                for (int x = 0; x < fig_width; x++) {
                    for (int y = 0; y < fig_height; y++) {
                        int[] rgb = new int[3];//临时存储RGB颜色
                        int nowX = i * fig_width + x;
                        int nowY = j * fig_height + y;
                        int pixel = bi.getRGB(nowX, nowY); // 下面三行代码将一个数字转换为RGB数字

                        rgb[0] = (pixel & 0xff0000) >> 16;
                        rgb[1] = (pixel & 0xff00) >> 8;
                        rgb[2] = (pixel & 0xff);

                        //如果是背景色
                        if ((Math.abs(bgc.getRed() - rgb[0]) < bg_alw)
                                && (Math.abs(bgc.getGreen() - rgb[1]) < bg_alw)
                                && (Math.abs(bgc.getBlue() - rgb[2]) < bg_alw)) {
                            //背景色设定

                        } else {
                            f_alw++;
                            //非背景色
                            //TODO:颜色判定优化
                            int color_value = color2Int(rgb);
                            c_value = c_value + color_value;

                        }
                    }
                }

                if (f_alw < fig_alw) {
                    continue;
                } else {
                    img_fig[i][j] = c_value % 36;
                }
            }
        }

        return img_fig;
    }

    //此处需要优化
    private int color2Int(int[] rgb){

        return (rgb[0] / 10 + (rgb[1] / 10) * 2 + (rgb[2] / 10) * 3);
    }

    /**
     * 画图
     *
     * @param img_fig    用于绘制的数组
     **/
    public BufferedImage drawByImgFig(int[][] img_fig) {

        String[] figure = getFigures();

        int f_width = width / fig_width;
        int f_height = height / fig_height;

        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = b.getGraphics();


      g.fillRect(0, 0, width, height);//填充整个屏幕

        g.setColor(paint_color);


       g.setFont(new Font(font_type, Font.LAYOUT_LEFT_TO_RIGHT, fig_font));

        for (int i = 0; i < f_width; i++) {
            for (int j = 0; j < f_height; j++) {

                //此处可根据情况修改画图模式
                if (img_fig[i][j] != -1) {
                    //若不是背景
                    String str = figure[img_fig[i][j]];
                    g.drawString(str, i * fig_width, fig_height + j * fig_height);
                } else {
                    //背景情况
            //         g.drawString(".", i * fig_width, fig_height + j * fig_height);
                }
            }
        }
        return b;
    }

    /**
     * 保存图片
     *
     * @param bi
     */
    public void saveImg(BufferedImage bi,String img_type, String img_out_path) {
        String out_path = img_out_path +"."+img_type;
        try {
            ImageIO.write(bi, img_type, new File(out_path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示的字符集合
     *
     * @return
     */
    private static String[] getFigures() {
        String figure[] = new String[36];

        //0-9 : 48-57
        for (int i = 0; i < 10; i++) {
            figure[i] = String.valueOf((char) (48 + i));
        }

        //A-Z : 65-90
        for (int i = 0; i < 26; i++) {
            figure[i + 10] = String.valueOf((char) (65 + i));
        }

        return figure;
    }


    /**
     * 将图片输出彩色
     *
     * @param bi_old
     * @param bi_new
     * @param pen_color
     * @return
     */
    public BufferedImage paintImgFigureColors(BufferedImage bi_old, BufferedImage bi_new, Color pen_color) {
        BufferedImage bi = bi_new;
        WritableRaster rasterDes = bi.getRaster();

        int alw = 20;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int pixel = bi_new.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字

                int[] rgb = new int[3];

                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                //   if(bgcolor[0]!=rgb[0]||bgcolor[1]!=rgb[1]||bgcolor[2]!=rgb[2])
                if (Math.abs(pen_color.getRed() - rgb[0]) < alw
                        && Math.abs(pen_color.getGreen() - rgb[1]) < alw
                        && Math.abs(pen_color.getBlue() - rgb[2]) < alw) {

                    int pixel_old = bi_old.getRGB(i, j);

                    rgb[0] = (pixel_old & 0xff0000) >> 16;
                    rgb[1] = (pixel_old & 0xff00) >> 8;
                    rgb[2] = (pixel_old & 0xff);

                    rasterDes.setPixel(i, j, rgb);
                }
            }
        }

        //导出图片
     /*   File fout = new File("C:\\Users\\vk\\Pictures\\test\\1234.JPG");
        RenderedImage renderedImage =  bi_new;
        try {
            ImageIO.write(renderedImage,TYPE_IMG_JPG,fout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        return bi;
    }


    /**
     * 为了提高效率，拿到img_fig,先判断里面是否有字符，如果有则进行处理，没有则跳过
     *
     * @param bi_old
     * @param bi_new

     * @param img_fig

     * @param pen_color
     * @return
     */
    public BufferedImage paintImgFigureColorsByImg_fig(BufferedImage bi_old, BufferedImage bi_new, int[][] img_fig, Color pen_color) {

        BufferedImage bi = bi_new;
        WritableRaster rasterDes = bi.getRaster();
        int f_width = width / fig_width;    //分组后，组的宽度
        int f_height = height / fig_height; //分组后，组的高度

        int alw = 20;

        for (int i = 0; i < f_width; i++) {
            for (int j = 0; j < f_height; j++) {
                //非背景像素组
                if (img_fig[i][j] != -1) {

                    for (int x = 0; x < fig_width; x++) {
                        for (int y = 0; y < fig_height; y++) {
                            int[] rgb = new int[3];//临时存储RGB颜色
                            int nowX = i * fig_width + x;
                            int nowY = j * fig_height + y;

                            int pixel = bi_new.getRGB(nowX, nowY);
                            rgb[0] = (pixel & 0xff0000) >> 16;
                            rgb[1] = (pixel & 0xff00) >> 8;
                            rgb[2] = (pixel & 0xff);

                            if (Math.abs(pen_color.getRed() - rgb[0]) < alw
                                    && Math.abs(pen_color.getGreen() - rgb[1]) < alw
                                    && Math.abs(pen_color.getBlue() - rgb[2]) < alw) {
                                int pixel_old = bi_old.getRGB(nowX, nowY);
                                rgb[0] = (pixel_old & 0xff0000) >> 16;
                                rgb[1] = (pixel_old & 0xff00) >> 8;
                                rgb[2] = (pixel_old & 0xff);
                                rasterDes.setPixel(nowX, nowY, rgb);
                            }
                        }
                    }
                }
            }
        }
        return bi;
    }

}
