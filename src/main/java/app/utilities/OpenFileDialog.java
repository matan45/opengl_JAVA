package app.utilities;

import app.utilities.logger.LogError;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class OpenFileDialog {

    private OpenFileDialog() {
    }


    public static Optional<Path> openFolder() {
        PointerBuffer outPath = memAllocPointer(1);

        return checkResult(NFD_PickFolder(outPath, (ByteBuffer) null), outPath);

    }

    public static Optional<Path> openFile(String filters, String name) {
        try (MemoryStack stack = stackPush()) {
            NFDFilterItem.Buffer filtersBuffer = NFDFilterItem.malloc(1);
            filtersBuffer.get(0)
                    .name(stack.UTF8(name))
                    .spec(stack.UTF8(filters));

            PointerBuffer outPath = memAllocPointer(1);

            return checkResult(NFD_OpenDialog(outPath, filtersBuffer, (ByteBuffer) null), outPath);

        }
    }

    public static Optional<Path> save(String filters, String name) {
        try (MemoryStack stack = stackPush()) {
            NFDFilterItem.Buffer filtersBuffer = NFDFilterItem.malloc(1);
            filtersBuffer.get(0)
                    .name(stack.UTF8(name))
                    .spec(stack.UTF8(filters));
            PointerBuffer savePath = memAllocPointer(1);

            return checkResult(NFD_SaveDialog(savePath, filtersBuffer, null, ""), savePath);
        }
    }

    public static List<Path> openMulti(String filters, String name) {
        try (MemoryStack stack = stackPush()) {
            NFDFilterItem.Buffer filtersObj = NFDFilterItem.malloc(1);
            filtersObj.get(0)
                    .name(stack.UTF8(name))
                    .spec(stack.UTF8(filters));
            PointerBuffer pathSet = stack.mallocPointer(1);

            int result = NFD_OpenDialogMultiple(pathSet, filtersObj, "");
            if (result == NFD_OKAY) {
                long path = pathSet.get(0);
                List<Path> paths = new ArrayList<>();
                NFDPathSetEnum psEnum = NFDPathSetEnum.calloc(stack);
                NFD_PathSet_GetEnum(path, psEnum);

                while (NFD_PathSet_EnumNext(psEnum, pathSet) == NFD_OKAY && pathSet.get(0) != NULL) {
                    paths.add(Path.of(Objects.requireNonNull(pathSet.getStringUTF8(0))));
                    NFD_PathSet_FreePath(pathSet.get(0));
                }

                NFD_PathSet_FreeEnum(psEnum);
                NFD_PathSet_Free(path);

                return paths;
            } else if (result == NFD_CANCEL)
                return Collections.emptyList();

            // NFD_ERROR
            LogError.println("Error: " + NFD_GetError());
            return Collections.emptyList();

        }
    }

    private static Optional<Path> checkResult(int result, PointerBuffer path) {
        StringBuilder pathResult;
        switch (result) {
            case NFD_OKAY -> {
                pathResult = new StringBuilder(path.getStringUTF8(0));
                NFD_FreePath(path.get(0));
                return Optional.of(Path.of(pathResult.toString()));
            }
            case NFD_CANCEL -> {
                return Optional.empty();
            }
            default -> {
                // NFD_ERROR
                LogError.println("Error: " + NFD_GetError());
                return Optional.empty();
            }
        }
    }
}
