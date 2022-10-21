package cn.com.moyu3390.core.fileservice.utils;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Iterator;

public class ImageUtil {

    public static void main(String[] args) {
        try {
            FileInputStream in  = new FileInputStream(new File("d:\\222222.png"));
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            byte[] bytes = removeBackground(data, 300, 0);
            FileOutputStream outputStream = new FileOutputStream(new File("d:\\2222222222222.png"));
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 去除图片背景色
     * @param fileByte
     * @param dpi
     * @param diaphaneity 透明度 0-100
     * @return
     * @throws IOException
     */
    public static byte[] removeBackground (byte[] fileByte, Integer dpi, Integer diaphaneity) throws IOException {

        ByteArrayInputStream image = new ByteArrayInputStream(fileByte);
        byte[] bytes;
        try {

            BufferedImage bi = ImageIO.read(image);

            ImageIcon imageIcon = new ImageIcon((Image) bi);

            BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),

                    BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();

            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());

            int alpha;
            String removeRgb = "";
        //这个背景底色的选择，我这里选择的是比较偏的位置，可以修改位置。背景色选择不知道有没有别的更优的方式（比如先过滤一遍获取颜色次数最多的，但是因为感觉做起来会比较复杂没去实现），如果有可以评论。
            int RGB=bufferedImage.getRGB(bufferedImage.getWidth()-1, bufferedImage.getHeight()-1);
            for(int i = bufferedImage.getMinX(); i < bufferedImage.getWidth(); i++) {
                for(int j = bufferedImage.getMinY(); j < bufferedImage.getHeight(); j++) {

                    int rgb = bufferedImage.getRGB(i, j);

                    int r = (rgb & 0xff0000) >>16;
                    int g = (rgb & 0xff00) >> 8;
                    int b = (rgb & 0xff);
                    int R = (RGB & 0xff0000) >>16;
                    int G = (RGB & 0xff00) >> 8;
                    int B = (RGB & 0xff);
                    //a为色差范围值，渐变色边缘处理，数值需要具体测试，50左右的效果比较可以
                    int a = 45;
                    if(Math.abs(R-r) < a && Math.abs(G-g) < a && Math.abs(B-b) < a ) {
                        alpha = 0;
                    } else {
                        alpha = 255;
                    }
                    rgb = (alpha << 24)|(rgb & 0x00ffffff);
                    bufferedImage.setRGB(i,j,rgb);
                }
            }
            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
            bytes = setDpi(bufferedImage, dpi).toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("处理图片背景失败");
        }
        return bytes;

    }


    private static String convertRgbStr(int color) {
        int red = (color & 0xff0000) >> 16;// 获取color(RGB)中R位
        int green = (color & 0x00ff00) >> 8;// 获取color(RGB)中G位
        int blue = (color & 0x0000ff);// 获取color(RGB)中B位
        return red + "," + green + "," + blue;
    }

    /**
     * 1英寸是2.54里面
     */
    private static final double INCH_2_CM = 2.54d;


    /**
     * 处理图片，设置图片DPI值
     * @param image
     * @param dpi dot per inch
     * @return
     * @throws IOException
     */
    private static ByteArrayOutputStream setDpi(BufferedImage image, int dpi) throws IOException {
        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_4BYTE_ABGR);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            setDPI(metadata, dpi);
            ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            } finally {
                stream.close();

            }
            return output;
        }
        return null;
    }

    /**
     * 设置图片的DPI值
     * @param metadata
     * @param dpi
     * @throws IIOInvalidTreeException
     * @return void
     */
    private static void setDPI(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {
        // for PMG, it's dots per millimeter
        double dotsPerMilli = 1.0 * dpi / 10 / INCH_2_CM;
        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

	public static byte[] colorImage(byte[] imageByte, Integer[] tricolor, String formatName) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(imageByte);
		BufferedImage image = ImageIO.read(in);
		
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);
				pixels[0] = tricolor[0];
				pixels[1] = tricolor[1];
				pixels[2] = tricolor[2];
				raster.setPixel(xx, yy, pixels);
			}
		}
		
		return setDpi(image, 600).toByteArray();
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		ImageIO.write(image, formatName, os);
//		return os.toByteArray();
	}

}
