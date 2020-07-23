import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class PictureHandler extends Handler {

    public PictureHandler(DataOutputStream outStream, DataInputStream inStream, String message, Server server, String input) {
        super(outStream, inStream, message, server, input);
    }

    @Override
    public void run() {
        try {
            if(message.equals("get picture")) {
                System.out.println("1");
                FileInputStream fileInputStream = new FileInputStream("src\\main\\java\\Status.png");
//                BufferedImage bufferedImage = ImageIO.read(fileInputStream);
//                System.out.println("Width : " + bufferedImage.getWidth());
//                System.out.println("Height : " + bufferedImage.getHeight());
                int i;
                System.out.println("2");
                while ((i = fileInputStream.read()) > -1) {
                    System.out.println("i : " + i);
                    outStream.write(i);
                    outStream.flush();
                }
                fileInputStream.close();
                outStream.close();
                System.out.println("3");
//                outStream.flush();
                System.out.println(new Date());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer[] getProfileImageArrayByUsername() {
        try {
            FileInputStream fileInputStream = new FileInputStream("src\\main\\java\\Status.png");
            ArrayList<Integer> imageArray = new ArrayList<>();

            int i;
            while ((i = fileInputStream.read()) > -1) {
                imageArray.add(i);
            }

            fileInputStream.close();

            Integer[] integers = new Integer[imageArray.size()];
            for (int j = 0; j < imageArray.size(); j++) {
                integers[j] = imageArray.get(j);
            }
            return integers;
        } catch (FileNotFoundException e) {
            //:)
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    @Override
    protected String handle() throws InterruptedException {
        return null;
    }


}
