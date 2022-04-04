package app.ecs.components;

import app.audio.Music;
import app.ecs.Entity;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Paths;

public class MusicComponent extends CommonComponent {
    private String path = "";
    private String prePath = "";
    private File file;

    private final Music music;


    public MusicComponent(Entity ownerEntity) {
        super(ownerEntity);
        file = new File("");
        music = new Music();
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Music"))
            path = OpenFileDialog.openFile("wav").orElse(prePath);

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
            music.loadMusic(Paths.get(path));
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.pushID("play");
        if (ImGui.button("play"))
            music.play();
        ImGui.popID();

        ImGui.pushID("pause");
        if (ImGui.button("pause"))
            music.pause();
        ImGui.popID();

        ImGui.pushID("stop");
        if (ImGui.button("stop"))
            music.stop();
        ImGui.popID();

        ImGui.pushID("volume");
        if (ImGui.button("Volume"))
            music.setVolume(0.5f);
        ImGui.sameLine();
        float[] volumeValue = {music.getVolume()};
        ImGui.dragFloat("##Y", volumeValue, 0.1f, 0.0f, 6f);
        music.setVolume(volumeValue[0]);
        ImGui.popID();


        ImGui.pushID("frame");
        int[] frameValue = {music.getFrame()};
        ImGui.dragInt("##Y", frameValue);
        ImGui.popID();

    }

    @Override
    public void cleanUp() {
        music.close();
    }


}
