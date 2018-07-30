import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Img2Fig {

    private Color bgc = Color.WHITE; //背景色，默认为白色，如果不想填背景色，则选择图片里的比较少见的颜色做背景色，或把容差改为负数

    //背景容差0-255，建议50-100，数值越大，背景判断越广
    private int bg_alw = 0;   //容差大于255时，则全为背景，如果不想有背景色，可以把bg_alw设置为负数

    //figure判定容差
    private int fig_alw = 0;

    private int imgWidth; //图片实际宽度
    private int imgHeight;//图片实际高度


    //一个字符的像素大小，默认是X5Y10
    private int fig_width = 5;
    private int fig_height = 10;
    private int font_size = 10; //关联字符大小
    private String font_type = "宋体";
    public Color paint_color = Color.BLACK;

    private int image_type = 5;


    /**
     * 默认无背景色模式，不推荐
     */
    public Img2Fig(){
        this.bg_alw = -1;
    }

    /**
     *
     * @param bgc 背景色
     * @param bg_alw 背景容差，范围为(0,1)
     */
    public Img2Fig(Color bgc, double bg_alw ){
        this.setBackground(bgc,bg_alw);
    }

    public Img2Fig(Color bgc, int bg_alw) {
       this.setBackground(bgc,bg_alw);
    }

    /**
     *设置背景参数
     * @param bgc
     * @param bg_alw 背景容差，范围为(0,255),若小于0，则无背景，不能大于1，若大于1，则无符合数据，不会生产图片
     */
    public void setBackground(Color bgc, int bg_alw) {
        this.bgc = bgc;
        if(bg_alw>=255) System.out.println("背景容差过大,请小于255，否则容易导致无符合的非背景像素，bg_alw = "+bg_alw);
        this.bg_alw = bg_alw;
    }

    public void setBackground(Color bgc, double bg_alw){
      this.setBackground(bgc,(int)(bg_alw*255));
    }


    /**
     * 设置字符大小，容差，fig_alw是相对于像素组而言（fig_width * fig_height）
     * @param fig_width
     * @param fig_height
     * @param fig_alw
     */
    public void setFigure(int fig_width, int fig_height, int fig_alw){
        this.fig_width = fig_width;
        this.fig_height = fig_height;
        this.fig_alw = fig_alw;
    }

    /**
     *
     * @param font_type 字体类型
     * @param font_size 字体大小
     * @param paint_color 图形颜色，在原色模式中此数据失效
     */
    public void setFont(String font_type,int font_size, Color paint_color){
        this.font_type = font_type;
        this.font_size = font_size;
        this.paint_color = paint_color;
    }


    /**
     * 设置新背景颜色，在原色模式中失效
     * @param color
     */
   public void setNewBackground(Color color){
        //TODO 设置新背景颜色
   }


    /**
     * 图片进行裁剪，（其实只是并未真正裁剪，只是不读其像素点）
     * 会影响图片大小
     * @return 返回一个BufferedImage对象
     */
    public BufferedImage imgRead(String img_path, String img_type) {
        img_path = img_path + "." + img_type;

        File file = new File(img_path);
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.imgWidth = bi.getWidth();
        this.imgHeight = bi.getHeight();

        //裁剪出多余的部分
        this.image_type = bi.getType();

        return bi;
    }

    /**
     * 设置图片宽高，这里多是批量处理一组图片，或者视频帧时，使用
     * @param width
     * @param height
     */
    public void setWH(int width, int height) {
        this.imgWidth = width;
        this.imgHeight = height;

    }

    /**
     * @param bi 图片对象
     * @return 返回二维数组记录每个字符信息，用于生成图片
     */
    public int[][] getImgFig(BufferedImage bi) {

        //开始像素minx,miny默认为0,预留以后更新从非0开始情况，而且这里要注意是用于对像素的循环，不是对像素组的循环
        int minx = 0;
        int miny = 0;

        int f_width = imgWidth / fig_width;    //分组后，组的宽度
        int f_height = imgHeight / fig_height; //分组后，组的高度

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

    //TODO 颜色->数字，
    private int color2Int(int[] rgb) {

        return (rgb[0] / 10 + (rgb[1] / 10) * 2 + (rgb[2] / 10) * 3);
    }

    /**
     * 画图
     *
     * @param img_fig 用于绘制的数组
     **/
    public BufferedImage drawByImgFig(int[][] img_fig) {

        String[] figure = getFigures();

        int f_width = imgWidth / fig_width;    //分组后，组的宽度
        int f_height = imgHeight / fig_height; //分组后，组的高度

        BufferedImage b = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = b.getGraphics();

        g.fillRect(0, 0, imgWidth, imgHeight);//填充整个屏幕

        g.setColor(paint_color);

        g.setFont(new Font(font_type, Font.LAYOUT_LEFT_TO_RIGHT, font_size));

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
     * 原色模式
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

        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {

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
        int f_width = imgWidth / fig_width;    //分组后，组的宽度
        int f_height = imgHeight / fig_height; //分组后，组的高度

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

    /**
     * 如果图片宽高和像素组不是正好整除，则需要对齐，这里并没有写对齐方法，可能存在右侧，下侧白边，可以用裁剪的方法。
     * @param bi
     * @return
     */
    public BufferedImage getCutImg(BufferedImage bi){

        int w = imgWidth - (imgWidth % fig_width);
        int h = imgHeight - (imgHeight % fig_height);

        BufferedImage b = new BufferedImage(w,h, BufferedImage.TYPE_3BYTE_BGR);

        for(int x = 0; x < w; x++){
            for(int y = 0; y < h ; y++ ){
                b.setRGB(x,y,bi.getRGB(x,y));
            }
        }
        return b;
    }


    /**
     * 保存图片
     *
     * @param bi
     */
    public void saveImg(BufferedImage bi, String img_type, String img_out_path) {
        String out_path = img_out_path + "." + img_type;
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


    public Color getBgc() {
        return bgc;
    }

    public int getBg_alw() {
        return bg_alw;
    }

    public int getFig_alw() {
        return fig_alw;
    }

    public int getFig_width() {
        return fig_width;
    }

    public int getFig_height() {
        return fig_height;
    }

    public int getFont_size() {
        return font_size;
    }

    public String getFont_type() {
        return font_type;
    }

    public Color getPaint_color() {
        return paint_color;
    }

    public int getImage_type() {
        return image_type;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

}
