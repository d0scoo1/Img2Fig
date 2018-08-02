### Img2Fig
## Java实现图片转字符图片

*对于一张背景和主体分明的图片，Img2Fig可以把背景和主体分离，然后把主体分成一个个像素组，转换成字符，合成图片。*

**一些需要了解的量**

    Color bgc = Color.WHITE; //背景色，默认为白色，如果不想填背景色，则选择图片里的比较少见的颜色做背景色，或把容差改为负数

    //背景容差0-255，建议25-100，数值越大，背景判断越广
    int bg_alw = 0;   //容差大于255时，则全为背景，如果不想有背景色，可以把bg_alw设置为负数

    //figure判定容差
    int fig_alw = 0;

    int imgWidth; //图片实际宽度
    int imgHeight;//图片实际高度

    //一个字符的像素大小，默认是X5Y10
    int fig_width = 5;
    int fig_height = 10;
    int font_size = 10; //关联字符大小
    String font_type = "宋体";
    Color paint_color = Color.BLACK;

    int image_type = 5;

**转换流程**</br>
目前只测试了JPG/JPEG、PNG（透明未加）这几种图片格式。</br>

 `setBackground(Color bgc, int bg_alw); `</br>
 设置背景颜色和容差，如果容差设置为负数，则设置的背景色无效</br>
 这是由于判断颜色的是通过RGB，`(Math.abs(bgc.getRed() - rgb[0])< bg_alw`恒成立</br>

 可以通过`setFont(String font_type,int font_size, Color paint_color);`来设置字体、字体大小和笔刷颜色。</br>
 更新了以后，记得使用`setFigure(int fig_width, int fig_height, int fig_alw);`来更新字符大小</br>
 因为不同的字体，和字体大小，占用的像素组是也不一样，需要我们根据经验设置</br>
 `fig_alw`容差是相对于`fig_width*fig_height`(像素组)而言的，这主要是用于边缘判定，对于默认的5\*10，则说明一个像素组大小为50，`fig_alw`大小则不能大于50，否则是不存在符合的像素组，对于边缘，我们假若设置为30，那么边缘小于的30的像素组，则被抛弃。</br>
 
 对像素进行分组，很容易产生对齐问题，假如，我们`fig_width`设置为5，那么对一个宽度为14像素的图片，则存在横向4个像素被抛弃，但我们生成的图片大小是和原图一样的，这会导致出现“白边”，对于这个问题，我们不打算改进（或者以后改进）。用户可以在保存图片前，使用`BufferedImage getCutImg(BufferedImage bi)`来获取裁剪后的图片。</br>

设置好参数后，通过`BufferedImage imgRead(String img_path, String img_type)`读取图片</br>
`int[][] getImgFig(BufferedImage bi)`会对图片进行处理，返回一个二维数组，这就是用于生成字符图片的数组（-1为背景，大于等于0，为字符）</br>
`BufferedImage drawByImgFig(int[][] img_fig)`返回`BufferedImage`图片对象，可以使用`saveImg(BufferedImage bi, String img_type, String img_out_path)`来保存，或者保存前使用`BufferedImage getCutImg(BufferedImage bi)`进行裁剪。


还提供`BufferedImage paintImgFigureColors(BufferedImage bi_old, BufferedImage bi_new, Color pen_color)`方法用于对生成的字符图片进行上色。需要提供`BufferedImage bi_old`即原图进行参考。</br>
如果图片中，字符较少，可以考虑使用</br>
`BufferedImage paintImgFigureColorsByImg_fig(BufferedImage bi_old, BufferedImage bi_new, int[][] img_fig, Color pen_color)`</br>
这可以提高性能。</br>

***
### AV2Fig
##Java实现视频转字符视频

*只测试过MP4格式视频，未测试其他格式视频，其他格式视频可能会有画质损失*</br>
*由于视频存在一些垃圾帧，最终视频的速率可能和原视频有出入*</br>

调用`av2FigAV(String avfile, String output, Img2Fig i2f)`对象就可以对传入的视频`avfile`转换成字符视频</br>
需要预先设置好`Img2Fig`参数</br>

