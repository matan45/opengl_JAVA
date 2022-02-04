package app.utilities;

import app.utilities.logger.LogError;
import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class OpenFileDialog {

    private OpenFileDialog() {
    }


    public static String openFolder() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            return checkResult(NFD_PickFolder((ByteBuffer) null, outPath), outPath);
        } finally {
            memFree(outPath);
        }
    }

    public static String openFile(String filters) {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            return checkResult(NFD_OpenDialog(filters, null, outPath), outPath);
        } finally {
            memFree(outPath);
        }
    }

    public static String save(String filters) {
        PointerBuffer savePath = memAllocPointer(1);

        try {
            return checkResult(NFD_SaveDialog(filters, null, savePath), savePath);
        } finally {
            memFree(savePath);
        }
    }

    private static String checkResult(int result, PointerBuffer path) {
        String pathResult;
        switch (result) {
            case NFD_OKAY -> {
                pathResult = path.getStringUTF8(0);
                nNFD_Free(path.get(0));
                return pathResult;
            }
            case NFD_CANCEL -> {
                return null;
            }
            default -> {
                // NFD_ERROR
                LogError.println("Error: " + NFD_GetError());
                return null;
            }
        }
    }
}
