import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GenTexture {
    public static void main(String[] args) {
        try {
            int width = 16;
            int height = 16;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            int[] palette = {
                0x00000000, // 0: transparent
                0xFF222222, // 1: black outline
                0xFFFFFFFF, // 2: white glass reflection
                0xFF88FFFF, // 3: light cyan liquid
                0xFF00AAAA, // 4: dark cyan liquid/edge
                0xFFAAAAAA, // 5: grey metal (needle)
                0xFFD2D2D2, // 6: light grey metal (plunger rod/glass)
                0xFF555555  // 7: dark grey plunger rubber
            };

            String[] gridStr = {
                "0000000000000111",
                "0000000000001661",
                "0000000000011510",
                "0000000000161000",
                "0000000001111000",
                "0000000016271000",
                "0000000162371000",
                "0000001623310000",
                "0000016233410000",
                "0000162334100000",
                "0000111111000000",
                "0001510000000000",
                "0015100000000000",
                "0151000000000000",
                "0110000000000000",
                "0000000000000000"
            };

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int colorIndex = Character.getNumericValue(gridStr[y].charAt(x));
                    image.setRGB(x, y, palette[colorIndex]);
                }
            }

            File outDir = new File("src/main/resources/assets/rsgauges/textures/item");
            outDir.mkdirs();
            File outFile = new File(outDir, "awesome_syringe.png");
            ImageIO.write(image, "png", outFile);
            System.out.println("Successfully generated texture at: " + outFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
