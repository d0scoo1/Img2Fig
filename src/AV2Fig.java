import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AV2Fig {

    public static void main(String args[]){
        System.out.println("开始视频转换");
        long start = System.currentTimeMillis();

        AV2Fig av2Fig = new AV2Fig();

        String avfile = "C:\\others\\code\\Video2Fig\\gg.mp4";
        String ouput = "C:\\others\\code\\Video2Fig\\test\\"+getTime()+".mp4";


        Img2Fig i2f = new Img2Fig();
        i2f.setBackground(Color.WHITE,80);
        i2f.setFigure(5,10,30);
        //i2f.setFont("宋体",10,Color.BLACK);

        try {
            av2Fig.av2FigAV(avfile, ouput, i2f);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("转换时间："+(System.currentTimeMillis() - start)/1000.0);
    }

    public AV2Fig(){

    }

    public void av2FigAV(String avfile, String output, Img2Fig i2f) throws Exception {

        //抓取
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(avfile);
        //需要先开始，才能获取初始化数据
        grabber.start();

        //设置Img2Fig处理图片的大小
        i2f.setWH(grabber.getImageWidth(),grabber.getImageHeight());

        //录制，需要输入，视频宽高，以及音频声道
        File outFile = new File(output);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outFile,grabber.getImageWidth(), grabber.getImageHeight(),grabber.getAudioChannels());

        /**录制参数设置**/
        //设置帧数，这里getFrameRate()直接获取的double类型帧数，不能直接用，会出错，可以输入 帧/秒 或者获取帧数取小数点1-2位
        DecimalFormat df = new DecimalFormat("#.0");
        double frameRate = Double.parseDouble(df.format(grabber.getFrameRate()));
        recorder.setFrameRate(frameRate);

        //recorder.setFormat("mp4"); //下面设置了编码格式，这里不进行设置

        if (grabber.hasVideo()){

            recorder.setVideoCodec(grabber.getVideoCodec()); //编码格式，很重要
            recorder.setVideoQuality(1.0D);//默认为-1.0D，设置为1.0D可以极大的提高画质！
            recorder.setVideoBitrate(grabber.getVideoBitrate()); //设置的必要性不大，因为视频转换以后，常常就改变了码率
        }

        if (grabber.hasAudio()) {

            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.setAudioChannels(grabber.getAudioChannels()); //如果创建录制对象时没有设置，这里一定要设置

            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setSampleRate(grabber.getSampleRate());

            recorder.setAudioQuality(1.0D);
        }
        /**录制参数设置完**/

        recorder.start();

        //用于转换Frame，bufferedImage
        Java2DFrameConverter converter = new Java2DFrameConverter();

        /**
        grabber.getLengthInVideoFrames();
        grabber.getLengthInAudioFrames();
        grabber.getLengthInFrames();
         这里慎重用这三个方法，好像依次获取后，会改变对象的一些参数，未作验证。
         **/

        Frame f;
        while ((f = grabber.grab()) != null) {

            //如果f.image存在，则说明是视频帧，否则为音频帧
            if (f.image != null) {

                BufferedImage bi = converter.getBufferedImage(f);
                BufferedImage new_bi = i2f.drawByImgFig(i2f.getImgFig(bi));

                Frame f_n = converter.getFrame(new_bi);

                f_n.timestamp = f.timestamp;

                recorder.record(f_n);

            } else {
                //音频直接录制
                recorder.record(f);
            }

        }

        recorder.stop();
        recorder.release();
        grabber.stop();
        grabber.release();


    }

    /**
     * 单独获取视频
     * @param i2f
     * @param avfile
     * @param output
     * @throws Exception
     */
    public void outVideo(Img2Fig i2f, String avfile, String output) throws Exception {

        Java2DFrameConverter converter = new Java2DFrameConverter();

        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(avfile);
        ff.start();

        i2f.setWH(ff.getImageWidth(), ff.getImageHeight());

        int length = ff.getLengthInVideoFrames();


        File outVideo = new File(output);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outVideo, ff.getImageWidth(), ff.getImageHeight());

        DecimalFormat df = new DecimalFormat("#.0");
        double frameRate = Double.parseDouble(df.format(ff.getFrameRate()));
        recorder.setFrameRate(frameRate);
        //  recorder.setFrameNumber(length);


        recorder.setVideoCodec(ff.getVideoCodec());
        recorder.setVideoQuality(1.0D);


        recorder.start();

        for(int i = 0 ; i <length; i++ ){

            Frame frame_grab = ff.grabImage();
            BufferedImage bi = converter.getBufferedImage(frame_grab);
            BufferedImage bi_fig = i2f.drawByImgFig(i2f.getImgFig(bi));

            Frame frame = converter.getFrame(bi_fig);

            frame.timestamp = frame_grab.timestamp;

            recorder.record(frame);

        }

        recorder.stop();
        recorder.release();
        ff.stop();
        ff.release();
    }


    /**
     * 单独获取音频
     * @param avfile
     * @param output
     * @throws Exception
     */
    public void outAudio(String avfile,String output) throws Exception{


        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(avfile);
        ff.start();

        int length = ff.getLengthInAudioFrames();

        File outAudio = new File(output);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outAudio,ff.getAudioChannels());

        DecimalFormat df = new DecimalFormat("#.0");
        double frameRate = Double.parseDouble(df.format(ff.getFrameRate()));
        recorder.setFrameRate(frameRate);


        recorder.setAudioCodec(ff.getAudioCodec());
        recorder.setAudioChannels(ff.getAudioChannels());
        recorder.setSampleRate(ff.getSampleRate());
        recorder.setAudioQuality(1.0D);

        recorder.start();

        for(int i = 0 ; i < length; i++){

            recorder.record(ff.grabSamples());
        }

        recorder.stop();
        recorder.release();
        ff.stop();
        ff.release();
    }




    /**
     * 获取所有的视频帧，暂时不用
     * @param avfile
     * @param i2f
     * @return
     * @throws FrameGrabber.Exception
     */
    private Frame[] getVideoFromAV(String avfile, Img2Fig i2f) throws FrameGrabber.Exception {

        Java2DFrameConverter converter = new Java2DFrameConverter();

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(avfile);
        grabber.start();

        int videoLength = grabber.getLengthInVideoFrames();

        Frame [] frames = new Frame[videoLength];

        i2f.setWH(grabber.getImageWidth(), grabber.getImageHeight());

        for(int i = 0 ; i < videoLength ; i++){

            Frame frame_grab = grabber.grabImage();
            BufferedImage bi = converter.getBufferedImage(frame_grab);
            BufferedImage bi_fig = i2f.drawByImgFig(i2f.getImgFig(bi));

            Frame frame = converter.getFrame(bi_fig);

            frame.timestamp = frame_grab.timestamp;

            frames[i] = frame;
        }

        return frames;
    }


    /**
     * 获取所有的音频帧，暂时不用
     * @param avfile
     * @return
     * @throws FrameGrabber.Exception
     */
    private Frame[] getAudioFromAV(String avfile) throws FrameGrabber.Exception {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(avfile);
        grabber.start();

        int audioLength = grabber.getLengthInAudioFrames();
        Frame[] frames = new Frame[audioLength];

        for(int i = 0 ; i < audioLength; i++){
            frames[i] = grabber.grabSamples();
        }

        return frames;
    }

    private static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");//设置日期格式
        String date = df.format(new Date());
        return date;
    }
}
