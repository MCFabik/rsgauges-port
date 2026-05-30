import java.nio.file.Files;
import java.nio.file.Path;

public class StripBom {
    public static void main(String[] args) throws Exception {
        Path p = Path.of("src/main/java/wile/rsgauges/datagen/ModRecipeProvider.java");
        byte[] bytes = Files.readAllBytes(p);
        if (bytes.length >= 3 && bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
            byte[] clean = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, clean, 0, clean.length);
            Files.write(p, clean);
            System.out.println("BOM stripped.");
        } else {
            System.out.println("No BOM found.");
        }
    }
}
