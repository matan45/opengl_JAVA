package app.ecs.components;

import app.audio.Music;
import app.ecs.Entity;
import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MusicComponent extends Component {
    private String path = "";
    private String prePath = "";
    private File file;

    private final Music music;

    private int totalLength = 0;
    private float maxV = 0;
    private float minV = 0;

    private final int playIcon;
    private final int stopIcon;
    private final int pauseIcon;


    public MusicComponent(Entity ownerEntity) {
        super(ownerEntity);
        file = new File("");
        music = new Music();

        Textures textures = EditorRenderer.getTextures();
        playIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\audio\\play.png"));
        stopIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\audio\\stop.png"));
        pauseIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\audio\\pause.png"));
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Music"))
            path = OpenFileDialog.openFile("wav","Music").orElse(Path.of(prePath)).toString();

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
            music.loadMusic(Paths.get(path));

            totalLength = music.getTotalFrame();
            maxV = music.getMaxVolume();
            minV = music.getMinVolume();
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.pushID("frame");
        int[] frameValue = {music.getFrame()};
        ImGui.sliderInt("##Y", frameValue, 0, totalLength, "%d%%");
        ImGui.popID();

        ImGui.columns(3, "", false);

        ImGui.pushID("play");
        if (ImGui.imageButton(playIcon, 32, 32))
            music.play();
        ImGui.popID();
        ImGui.nextColumn();

        ImGui.pushID("pause");
        if (ImGui.imageButton(pauseIcon, 32, 32))
            music.pause();
        ImGui.popID();
        ImGui.nextColumn();

        ImGui.pushID("stop");
        if (ImGui.imageButton(stopIcon, 32, 32))
            music.stop();
        ImGui.popID();

        ImGui.columns(1);

        ImGui.pushID("volume");
        if (ImGui.button("Volume"))
            music.setVolume(0f);
        ImGui.sameLine();
        float[] volumeValue = {music.getVolume()};
        ImGui.dragFloat("##Y", volumeValue, 0.1f, minV, maxV);
        music.setVolume(volumeValue[0]);
        ImGui.popID();

    }

    @Override
    public void cleanUp() {
        music.close();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
